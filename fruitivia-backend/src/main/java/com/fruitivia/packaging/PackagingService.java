package com.fruitivia.packaging;

import com.fruitivia.batch.FruitBatch;
import com.fruitivia.batch.FruitBatchRepository;
import com.fruitivia.common.event.EventPublisher;
import com.fruitivia.inventory.InventoryService;
import com.fruitivia.inventory.dto.InventoryDto;
import com.fruitivia.inventory.dto.InventoryOperationRequest;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderItem;
import com.fruitivia.order.OrderItemAllocation;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.packaging.dto.*;
import com.fruitivia.packaging.event.PackagingCompletedEvent;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackagingService {

    private final PackagingRecordRepository packagingRepository;
    private final PackagingStatusHistoryRepository historyRepository;
    private final OrderRepository orderRepository;
    private final FruitBatchRepository batchRepository;
    private final InventoryService inventoryService;
    private final EventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Transactional
    public PackagingRecordDto createPackagingRecord(PackagingCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Validate quantities
        validatePackagingQuantities(order, request.getItems());

        PackagingRecord record = PackagingRecord.builder()
                .order(order)
                .status(PackagingStatus.PENDING)
                .packingDate(request.getPackingDate())
                .packedBy(request.getPackedBy())
                .notes(request.getNotes())
                .build();
        record.setActive(true);

        for (PackagingItemRequest itemRequest : request.getItems()) {
            FruitBatch batch = batchRepository.findById(itemRequest.getFruitBatchId())
                    .orElseThrow(() -> new EntityNotFoundException("Batch not found: " + itemRequest.getFruitBatchId()));

            PackagingItem item = PackagingItem.builder()
                    .fruitBatch(batch)
                    .quantity(itemRequest.getQuantity())
                    .packagingType(itemRequest.getPackagingType())
                    .packageCount(itemRequest.getPackageCount())
                    .packageWeight(itemRequest.getPackageWeight())
                    .dimensions(itemRequest.getDimensions())
                    .build();
            item.setActive(true);
            record.addItem(item);
        }

        PackagingRecord saved = packagingRepository.save(record);
        saveHistory(saved, PackagingStatus.PENDING, "Packaging record created");

        return mapToDto(saved);
    }

    @Transactional
    public PackagingRecordDto updateStatus(UUID id, PackagingStatusUpdateRequest request) {
        PackagingRecord record = packagingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Packaging record not found"));

        PackagingStatus current = record.getStatus();
        PackagingStatus target = request.getStatus();

        if (current == target) {
            return mapToDto(record);
        }

        if (current == PackagingStatus.COMPLETED || current == PackagingStatus.REJECTED) {
            throw new IllegalStateException("Cannot transition from terminal status: " + current);
        }

        // Apply strict state transitions
        if (current == PackagingStatus.PENDING && target != PackagingStatus.IN_PROGRESS && target != PackagingStatus.REJECTED) {
            throw new IllegalStateException("PENDING can only transition to IN_PROGRESS or REJECTED");
        }
        if (current == PackagingStatus.IN_PROGRESS && target != PackagingStatus.COMPLETED && target != PackagingStatus.REJECTED) {
            throw new IllegalStateException("IN_PROGRESS can only transition to COMPLETED or REJECTED");
        }

        record.setStatus(target);
        record = packagingRepository.save(record);
        saveHistory(record, target, request.getNotes());

        if (target == PackagingStatus.COMPLETED) {
            handlePackagingCompletion(record);
        }

        return mapToDto(record);
    }

    @Transactional(readOnly = true)
    public List<PackagingRecordDto> getPackagingRecordsByOrder(UUID orderId) {
        return packagingRepository.findByOrderId(orderId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void validatePackagingQuantities(Order order, List<PackagingItemRequest> requestedItems) {
        // 1. Calculate allocated quantity per batch for this order
        Map<UUID, BigDecimal> allocatedPerBatch = order.getItems().stream()
                .flatMap(item -> item.getAllocations().stream())
                .collect(Collectors.groupingBy(
                        alloc -> alloc.getFruitBatch().getId(),
                        Collectors.reducing(BigDecimal.ZERO, OrderItemAllocation::getQuantity, BigDecimal::add)
                ));

        // 2. Calculate already packed or pending to pack quantity per batch for this order
        List<PackagingRecord> existingRecords = packagingRepository.findByOrderId(order.getId());
        Map<UUID, BigDecimal> alreadyPackedPerBatch = existingRecords.stream()
                .filter(r -> r.getStatus() != PackagingStatus.REJECTED)
                .flatMap(r -> r.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getFruitBatch().getId(),
                        Collectors.reducing(BigDecimal.ZERO, PackagingItem::getQuantity, BigDecimal::add)
                ));

        // 3. Sum up the requested quantities per batch
        Map<UUID, BigDecimal> requestedPerBatch = requestedItems.stream()
                .collect(Collectors.groupingBy(
                        PackagingItemRequest::getFruitBatchId,
                        Collectors.reducing(BigDecimal.ZERO, PackagingItemRequest::getQuantity, BigDecimal::add)
                ));

        // 4. Validate
        for (Map.Entry<UUID, BigDecimal> entry : requestedPerBatch.entrySet()) {
            UUID batchId = entry.getKey();
            BigDecimal requestedQty = entry.getValue();

            BigDecimal allocated = allocatedPerBatch.getOrDefault(batchId, BigDecimal.ZERO);
            BigDecimal alreadyPacked = alreadyPackedPerBatch.getOrDefault(batchId, BigDecimal.ZERO);

            if (allocated.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Batch " + batchId + " is not allocated to this order");
            }

            BigDecimal totalAfterRequest = alreadyPacked.add(requestedQty);
            if (totalAfterRequest.compareTo(allocated) > 0) {
                throw new IllegalArgumentException("Cannot pack more than allocated for batch " + batchId + ". Allocated: " + allocated + ", Already packed/pending: " + alreadyPacked + ", Requested: " + requestedQty);
            }
        }
    }

    private void handlePackagingCompletion(PackagingRecord record) {
        // Update Inventory
        for (PackagingItem item : record.getItems()) {
            InventoryDto inventory = inventoryService.getInventoryByBatchId(item.getFruitBatch().getId());
            
            InventoryOperationRequest req = new InventoryOperationRequest();
            req.setQuantity(item.getQuantity());
            req.setReason("Packaging completed");
            req.setReference(record.getId().toString());
            
            inventoryService.pack(inventory.getId(), req);
        }

        // Publish event
        eventPublisher.publish(new PackagingCompletedEvent(this, record.getId(), record.getOrder().getId()));
    }

    private void saveHistory(PackagingRecord record, PackagingStatus status, String notes) {
        User actor = getCurrentUser();
        PackagingStatusHistory history = PackagingStatusHistory.builder()
                .packagingRecord(record)
                .status(status)
                .actor(actor)
                .notes(notes)
                .build();
        history.setActive(true);
        historyRepository.save(history);
    }

    private User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    private PackagingRecordDto mapToDto(PackagingRecord record) {
        return PackagingRecordDto.builder()
                .id(record.getId())
                .orderId(record.getOrder().getId())
                .orderNumber(record.getOrder().getOrderNumber())
                .status(record.getStatus())
                .packingDate(record.getPackingDate())
                .packedBy(record.getPackedBy())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .items(record.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList()))
                .build();
    }

    private PackagingItemDto mapItemToDto(PackagingItem item) {
        return PackagingItemDto.builder()
                .id(item.getId())
                .fruitBatchId(item.getFruitBatch().getId())
                .batchNumber(item.getFruitBatch().getBatchNumber())
                .quantity(item.getQuantity())
                .packagingType(item.getPackagingType())
                .packageCount(item.getPackageCount())
                .packageWeight(item.getPackageWeight())
                .dimensions(item.getDimensions())
                .build();
    }
}
