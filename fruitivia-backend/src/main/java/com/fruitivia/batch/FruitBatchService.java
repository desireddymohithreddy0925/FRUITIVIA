package com.fruitivia.batch;

import com.fruitivia.batch.dto.FruitBatchCreateRequest;
import com.fruitivia.batch.dto.FruitBatchDto;
import com.fruitivia.batch.dto.FruitBatchStatusUpdateRequest;
import com.fruitivia.quality.InspectionStatus;
import com.fruitivia.quality.QualityInspection;
import com.fruitivia.quality.QualityInspectionRepository;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.warehouse.WarehouseRepository;
import com.fruitivia.common.event.EventPublisher;
import com.fruitivia.batch.event.BatchNearExpiryEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FruitBatchService {

    private final FruitBatchRepository batchRepository;
    private final FruitBatchHistoryRepository historyRepository;
    private final FruitBatchSequenceService sequenceService;
    private final QualityInspectionRepository qualityInspectionRepository;
    private final WarehouseRepository warehouseRepository;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<FruitBatchDto> getAllBatches(com.fruitivia.batch.dto.FruitBatchFilter filter, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<FruitBatch> spec = org.springframework.data.jpa.domain.Specification.where(FruitBatchSpecification.build(filter))
                .and((root, query, cb) -> cb.isTrue(root.get("active")));
        return batchRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public FruitBatchDto getBatchById(UUID id) {
        return batchRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Fruit Batch not found"));
    }

    @Transactional
    public FruitBatchDto createBatch(FruitBatchCreateRequest request, String currentUserEmail) {
        QualityInspection inspection = qualityInspectionRepository.findById(request.getQualityInspectionId())
                .orElseThrow(() -> new EntityNotFoundException("Quality inspection not found"));

        if (inspection.getStatus() == InspectionStatus.FAILED || inspection.getStatus() == InspectionStatus.REQUIRES_REINSPECTION || inspection.getStatus() == InspectionStatus.PENDING) {
            throw new IllegalArgumentException("Cannot create a batch from a Quality Inspection with status: " + inspection.getStatus());
        }

        if (request.getApprovedQuantity().compareTo(inspection.getAcceptedQuantity()) > 0) {
            throw new IllegalArgumentException("Approved quantity cannot exceed the QC accepted quantity");
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        // Generate Prefix based on Fruit code/name (e.g., first 3 letters)
        String prefix = inspection.getProcurementItem().getFruit().getName().substring(0, Math.min(3, inspection.getProcurementItem().getFruit().getName().length())).toUpperCase();
        String batchNumber = sequenceService.generateBatchNumber(prefix);

        Integer shelfLifeDays = inspection.getProcurementItem().getVariety().getShelfLifeDays();
        Instant expirationDate = request.getPackingDate().plus(shelfLifeDays, ChronoUnit.DAYS);

        FruitBatch batch = FruitBatch.builder()
                .batchNumber(batchNumber)
                .procurementItem(inspection.getProcurementItem())
                .supplier(inspection.getProcurementItem().getProcurementOrder().getSupplier())
                .fruit(inspection.getProcurementItem().getFruit())
                .variety(inspection.getProcurementItem().getVariety())
                .qualityInspection(inspection)
                .qualityGrade(inspection.getGrade())
                .approvedQuantity(request.getApprovedQuantity())
                .warehouse(warehouse)
                .harvestDate(inspection.getProcurementItem().getProcurementOrder().getHarvestDate()) // fallback to PO harvest date
                .packingDate(request.getPackingDate())
                .shelfLifeDays(shelfLifeDays)
                .expirationDate(expirationDate)
                .status(BatchStatus.CREATED)
                .build();
        batch.setActive(true);

        FruitBatch savedBatch = batchRepository.save(batch);
        saveHistory(savedBatch, null, BatchStatus.CREATED, currentUserEmail, "Batch created");

        return mapToDto(savedBatch);
    }

    @Transactional
    public FruitBatchDto updateStatus(UUID id, FruitBatchStatusUpdateRequest request, String currentUserEmail) {
        FruitBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fruit Batch not found"));

        BatchStatus oldStatus = batch.getStatus();
        BatchStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return mapToDto(batch);
        }

        if (oldStatus == BatchStatus.EXPIRED || oldStatus == BatchStatus.CANCELLED || oldStatus == BatchStatus.SHIPPED) {
            throw new IllegalArgumentException("Cannot transition out of terminal state: " + oldStatus);
        }
        
        // Dynamic expiration check
        if (Instant.now().isAfter(batch.getExpirationDate()) && newStatus != BatchStatus.EXPIRED && newStatus != BatchStatus.CANCELLED) {
            throw new IllegalArgumentException("Batch is expired. It can only be transitioned to EXPIRED or CANCELLED.");
        }

        batch.setStatus(newStatus);
        FruitBatch savedBatch = batchRepository.save(batch);

        saveHistory(savedBatch, oldStatus, newStatus, currentUserEmail, request.getComments());

        return mapToDto(savedBatch);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkNearExpiryBatches() {
        Instant threshold = Instant.now().plus(7, ChronoUnit.DAYS);
        batchRepository.findAll((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("status"), BatchStatus.CREATED), // Or other non-terminal states
                cb.lessThanOrEqualTo(root.get("expirationDate"), threshold),
                cb.greaterThan(root.get("expirationDate"), Instant.now())
            )
        ).forEach(batch -> eventPublisher.publish(new BatchNearExpiryEvent(this, batch.getId())));
    }

    private void saveHistory(FruitBatch batch, BatchStatus prev, BatchStatus current, String userEmail, String comments) {
        FruitBatchHistory history = FruitBatchHistory.builder()
                .fruitBatch(batch)
                .previousStatus(prev)
                .newStatus(current)
                .changedBy(userEmail)
                .comments(comments)
                .build();
        history.setActive(true);
        historyRepository.save(history);
    }

    private FruitBatchDto mapToDto(FruitBatch batch) {
        return FruitBatchDto.builder()
                .id(batch.getId())
                .batchNumber(batch.getBatchNumber())
                .procurementItemId(batch.getProcurementItem().getId())
                .supplierId(batch.getSupplier().getId())
                .supplierName(batch.getSupplier().getName())
                .fruitId(batch.getFruit().getId())
                .fruitName(batch.getFruit().getName())
                .varietyId(batch.getVariety().getId())
                .varietyName(batch.getVariety().getName())
                .qualityInspectionId(batch.getQualityInspection().getId())
                .qualityGrade(batch.getQualityGrade())
                .approvedQuantity(batch.getApprovedQuantity())
                .warehouseId(batch.getWarehouse().getId())
                .warehouseName(batch.getWarehouse().getName())
                .harvestDate(batch.getHarvestDate())
                .packingDate(batch.getPackingDate())
                .shelfLifeDays(batch.getShelfLifeDays())
                .expirationDate(batch.getExpirationDate())
                .status(batch.getStatus())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .build();
    }
}
