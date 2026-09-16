package com.fruitivia.inventory.dto;

import com.fruitivia.inventory.InventoryTransactionType;
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
public class InventoryTransactionDto {
    private UUID id;
    private UUID inventoryId;
    private InventoryTransactionType type;
    private BigDecimal transactionQuantity;
    private BigDecimal beforeQuantity;
    private BigDecimal afterQuantity;
    private String reason;
    private String reference;
    private String actor;
    private Instant createdAt;
}
