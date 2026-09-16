package com.fruitivia.warehouse.dto;

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
public class StorageLocationDto {
    private UUID id;
    private String locationCode;
    private UUID warehouseId;
    private String warehouseName;
    private BigDecimal capacity;
    private BigDecimal currentUsage;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
