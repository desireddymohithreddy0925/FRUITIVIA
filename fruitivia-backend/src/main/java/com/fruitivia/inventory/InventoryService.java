package com.fruitivia.inventory;

import com.fruitivia.batch.FruitBatch;
import com.fruitivia.batch.FruitBatchRepository;
import com.fruitivia.inventory.dto.InventoryDto;
import com.fruitivia.inventory.dto.InventoryOperationRequest;
import com.fruitivia.inventory.dto.InventoryTransactionDto;
import com.fruitivia.warehouse.StorageLocation;
import com.fruitivia.warehouse.StorageLocationRepository;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.common.event.EventPublisher;
import com.fruitivia.inventory.event.InventoryLowEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final FruitBatchRepository batchRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<InventoryDto> getAllInventories(com.fruitivia.inventory.dto.InventoryFilter filter, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Inventory> spec = org.springframework.data.jpa.domain.Specification.where(InventorySpecification.build(filter))
                .and((root, query, cb) -> cb.isTrue(root.get("active")));
        return inventoryRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryById(UUID id) {
        return inventoryRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryByBatchId(UUID batchId) {
        return inventoryRepository.findByBatchId(batchId)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for this batch"));
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionDto> getTransactions(UUID inventoryId) {
        if (!inventoryRepository.existsById(inventoryId)) {
            throw new EntityNotFoundException("Inventory not found");
        }
        return transactionRepository.findByInventoryIdOrderByCreatedAtDesc(inventoryId)
                .stream()
                .map(this::mapToTransactionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDto receiveBatch(UUID batchId, UUID storageLocationId, InventoryOperationRequest request) {
        FruitBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found"));
        
        if (inventoryRepository.findByBatchId(batchId).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for this batch");
        }

        Warehouse warehouse = batch.getWarehouse();
        StorageLocation location = null;
        if (storageLocationId != null) {
            location = storageLocationRepository.findById(storageLocationId)
                    .orElseThrow(() -> new EntityNotFoundException("Storage location not found"));
            if (!location.getWarehouse().getId().equals(warehouse.getId())) {
                throw new IllegalArgumentException("Storage location does not belong to the batch's warehouse");
            }
        }

        Inventory inventory = Inventory.builder()
                .batch(batch)
                .warehouse(warehouse)
                .storageLocation(location)
                .totalQuantity(request.getQuantity())
                .availableQuantity(request.getQuantity())
                .reservedQuantity(BigDecimal.ZERO)
                .packedQuantity(BigDecimal.ZERO)
                .dispatchedQuantity(BigDecimal.ZERO)
                .damagedQuantity(BigDecimal.ZERO)
                .expiredQuantity(BigDecimal.ZERO)
                .build();
        inventory.setActive(true);
        inventory = inventoryRepository.save(inventory);

        recordTransaction(inventory, InventoryTransactionType.RECEIVE, request.getQuantity(), 
                BigDecimal.ZERO, request.getQuantity(), request.getReason(), request.getReference());

        return mapToDto(inventory);
    }

    @Transactional
    public InventoryDto reserve(UUID inventoryId, InventoryOperationRequest request) {
        Inventory inventory = getInventoryWithLock(inventoryId);
        
        BigDecimal qty = request.getQuantity();
        if (inventory.getAvailableQuantity().compareTo(qty) < 0) {
            throw new IllegalArgumentException("Cannot reserve more than available quantity");
        }
        
        if (inventory.getBatch().getExpirationDate() != null && 
            java.time.Instant.now().isAfter(inventory.getBatch().getExpirationDate())) {
            throw new IllegalArgumentException("Cannot reserve expired inventory");
        }

        BigDecimal before = inventory.getAvailableQuantity();
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(qty));
        inventory.setReservedQuantity(inventory.getReservedQuantity().add(qty));
        
        inventoryRepository.save(inventory);

        recordTransaction(inventory, InventoryTransactionType.RESERVE, qty, before, inventory.getAvailableQuantity(), 
                request.getReason(), request.getReference());

        if (before.compareTo(new BigDecimal("100.00")) >= 0 && inventory.getAvailableQuantity().compareTo(new BigDecimal("100.00")) < 0) {
            eventPublisher.publish(new InventoryLowEvent(this, inventory.getBatch().getFruit().getId(), inventory.getBatch().getVariety().getId()));
        }

        return mapToDto(inventory);
    }

    @Transactional
    public InventoryDto release(UUID inventoryId, InventoryOperationRequest request) {
        Inventory inventory = getInventoryWithLock(inventoryId);
        
        BigDecimal qty = request.getQuantity();
        if (inventory.getReservedQuantity().compareTo(qty) < 0) {
            throw new IllegalArgumentException("Cannot release more than reserved quantity");
        }

        BigDecimal before = inventory.getAvailableQuantity();
        inventory.setReservedQuantity(inventory.getReservedQuantity().subtract(qty));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(qty));
        
        inventoryRepository.save(inventory);

        recordTransaction(inventory, InventoryTransactionType.RELEASE, qty, before, inventory.getAvailableQuantity(), 
                request.getReason(), request.getReference());

        return mapToDto(inventory);
    }

    @Transactional
    public List<com.fruitivia.inventory.dto.InventoryAllocationResult> allocateFIFO(UUID fruitId, UUID varietyId, BigDecimal requiredQuantity, String reason, String reference) {
        List<Inventory> availableInventories = inventoryRepository.findAvailableInventoryByFruitAndVariety(fruitId, varietyId);
        List<com.fruitivia.inventory.dto.InventoryAllocationResult> results = new java.util.ArrayList<>();
        
        BigDecimal remainingToAllocate = requiredQuantity;
        
        for (Inventory inventory : availableInventories) {
            if (remainingToAllocate.compareTo(BigDecimal.ZERO) <= 0) break;
            
            // Re-fetch with lock
            Inventory lockedInventory = getInventoryWithLock(inventory.getId());
            BigDecimal available = lockedInventory.getAvailableQuantity();
            
            if (available.compareTo(BigDecimal.ZERO) <= 0) continue;
            
            if (lockedInventory.getBatch().getExpirationDate() != null && 
                java.time.Instant.now().isAfter(lockedInventory.getBatch().getExpirationDate())) {
                continue;
            }
            
            BigDecimal allocateAmt = remainingToAllocate.min(available);
            
            BigDecimal before = lockedInventory.getAvailableQuantity();
            lockedInventory.setAvailableQuantity(lockedInventory.getAvailableQuantity().subtract(allocateAmt));
            lockedInventory.setReservedQuantity(lockedInventory.getReservedQuantity().add(allocateAmt));
            
            inventoryRepository.save(lockedInventory);
            
            recordTransaction(lockedInventory, InventoryTransactionType.RESERVE, allocateAmt, before, lockedInventory.getAvailableQuantity(), reason, reference);

            if (before.compareTo(new BigDecimal("100.00")) >= 0 && lockedInventory.getAvailableQuantity().compareTo(new BigDecimal("100.00")) < 0) {
                eventPublisher.publish(new InventoryLowEvent(this, lockedInventory.getBatch().getFruit().getId(), lockedInventory.getBatch().getVariety().getId()));
            }
            
            results.add(com.fruitivia.inventory.dto.InventoryAllocationResult.builder()
                    .inventoryId(lockedInventory.getId())
                    .batchId(lockedInventory.getBatch().getId())
                    .allocatedQuantity(allocateAmt)
                    .build());
                    
            remainingToAllocate = remainingToAllocate.subtract(allocateAmt);
        }
        
        if (remainingToAllocate.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Insufficient inventory available. Required: " + requiredQuantity + ", Shortfall: " + remainingToAllocate);
        }
        
        return results;
    }

    @Transactional
    public InventoryDto pack(UUID inventoryId, InventoryOperationRequest request) {
        Inventory inventory = getInventoryWithLock(inventoryId);
        
        BigDecimal qty = request.getQuantity();
        if (inventory.getReservedQuantity().compareTo(qty) < 0) {
            throw new IllegalArgumentException("Cannot pack more than reserved quantity");
        }

        BigDecimal before = inventory.getPackedQuantity();
        inventory.setReservedQuantity(inventory.getReservedQuantity().subtract(qty));
        inventory.setPackedQuantity(inventory.getPackedQuantity().add(qty));
        
        inventoryRepository.save(inventory);

        recordTransaction(inventory, InventoryTransactionType.PACK, qty, before, inventory.getPackedQuantity(), 
                request.getReason(), request.getReference());

        return mapToDto(inventory);
    }

    private Inventory getInventoryWithLock(UUID inventoryId) {
        return inventoryRepository.findByIdWithPessimisticWriteLock(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
    }

    private void recordTransaction(Inventory inventory, InventoryTransactionType type, BigDecimal txQty, 
                                   BigDecimal beforeQty, BigDecimal afterQty, String reason, String ref) {
        String actor = getCurrentUserEmail();
        InventoryTransaction tx = InventoryTransaction.builder()
                .inventory(inventory)
                .type(type)
                .transactionQuantity(txQty)
                .beforeQuantity(beforeQty)
                .afterQuantity(afterQty)
                .reason(reason)
                .reference(ref)
                .actor(actor)
                .build();
        tx.setActive(true);
        transactionRepository.save(tx);
    }

    private String getCurrentUserEmail() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "system";
    }

    private InventoryDto mapToDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .batchId(inventory.getBatch().getId())
                .batchNumber(inventory.getBatch().getBatchNumber())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .storageLocationId(inventory.getStorageLocation() != null ? inventory.getStorageLocation().getId() : null)
                .storageLocationCode(inventory.getStorageLocation() != null ? inventory.getStorageLocation().getLocationCode() : null)
                .totalQuantity(inventory.getTotalQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .packedQuantity(inventory.getPackedQuantity())
                .dispatchedQuantity(inventory.getDispatchedQuantity())
                .damagedQuantity(inventory.getDamagedQuantity())
                .expiredQuantity(inventory.getExpiredQuantity())
                .active(inventory.isActive())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private InventoryTransactionDto mapToTransactionDto(InventoryTransaction tx) {
        return InventoryTransactionDto.builder()
                .id(tx.getId())
                .inventoryId(tx.getInventory().getId())
                .type(tx.getType())
                .transactionQuantity(tx.getTransactionQuantity())
                .beforeQuantity(tx.getBeforeQuantity())
                .afterQuantity(tx.getAfterQuantity())
                .reason(tx.getReason())
                .reference(tx.getReference())
                .actor(tx.getActor())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
