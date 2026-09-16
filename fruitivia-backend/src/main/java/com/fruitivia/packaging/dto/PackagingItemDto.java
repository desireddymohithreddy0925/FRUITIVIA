package com.fruitivia.packaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingItemDto {
    private UUID id;
    private UUID fruitBatchId;
    private String batchNumber;
    private BigDecimal quantity;
    private String packagingType;
    private Integer packageCount;
    private BigDecimal packageWeight;
    private String dimensions;
}
