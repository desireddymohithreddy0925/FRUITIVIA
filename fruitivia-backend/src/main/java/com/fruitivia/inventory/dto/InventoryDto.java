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
public class InventoryDto {
    private UUID id;
    private UUID batchId;
    private String batchNumber;
    private UUID warehouseId;
    private String warehouseName;
    private UUID storageLocationId;
    private String storageLocationCode;

    private BigDecimal totalQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal reservedQuantity;
    private BigDecimal packedQuantity;
    private BigDecimal dispatchedQuantity;
    private BigDecimal damagedQuantity;
    private BigDecimal expiredQuantity;

    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
