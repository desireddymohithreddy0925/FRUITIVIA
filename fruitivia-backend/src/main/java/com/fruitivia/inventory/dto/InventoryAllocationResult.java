package com.fruitivia.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InventoryAllocationResult {
    private UUID inventoryId;
    private UUID batchId;
    private BigDecimal allocatedQuantity;
}
