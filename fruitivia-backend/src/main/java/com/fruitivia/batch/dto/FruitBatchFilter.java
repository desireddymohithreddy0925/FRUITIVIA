package com.fruitivia.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FruitBatchFilter {
    private UUID fruitId;
    private UUID varietyId;
    private String qualityGrade;
    private Instant harvestDateFrom;
    private Instant harvestDateTo;
    private Instant expirationDateFrom;
    private Instant expirationDateTo;
    private Integer remainingShelfLifeDays;
    private UUID warehouseId;
    private String status;
    private UUID supplierId;
}
