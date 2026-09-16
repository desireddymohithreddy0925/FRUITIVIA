package com.fruitivia.batch.dto;

import com.fruitivia.batch.BatchStatus;
import com.fruitivia.quality.QualityGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FruitBatchDto {
    private UUID id;
    private String batchNumber;
    private UUID procurementItemId;
    private UUID supplierId;
    private String supplierName;
    private UUID fruitId;
    private String fruitName;
    private UUID varietyId;
    private String varietyName;
    private UUID qualityInspectionId;
    private QualityGrade qualityGrade;
    private BigDecimal approvedQuantity;
    private UUID warehouseId;
    private String warehouseName;
    private Instant harvestDate;
    private Instant packingDate;
    private Integer shelfLifeDays;
    private Instant expirationDate;
    private BatchStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
