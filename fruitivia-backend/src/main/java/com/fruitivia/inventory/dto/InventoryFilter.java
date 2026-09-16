package com.fruitivia.inventory.dto;

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
public class InventoryFilter {
    private BigDecimal minAvailableQuantity;
    private BigDecimal maxAvailableQuantity;
    private BigDecimal minReservedQuantity;
    private BigDecimal maxReservedQuantity;
    private Instant expiryDateFrom;
    private Instant expiryDateTo;
    private UUID warehouseId;
    private String qualityGrade;
}
