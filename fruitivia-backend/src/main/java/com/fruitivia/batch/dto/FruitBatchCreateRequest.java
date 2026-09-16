package com.fruitivia.batch.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class FruitBatchCreateRequest {

    @NotNull(message = "Quality Inspection ID is required")
    private UUID qualityInspectionId;

    @NotNull(message = "Approved quantity is required")
    @PositiveOrZero(message = "Approved quantity must be non-negative")
    private BigDecimal approvedQuantity;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Packing date is required")
    private Instant packingDate;

}
