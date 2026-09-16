package com.fruitivia.packaging.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PackagingItemRequest {
    
    @NotNull(message = "Fruit batch ID is required")
    private UUID fruitBatchId;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;

    @NotBlank(message = "Packaging type is required")
    private String packagingType;

    @NotNull(message = "Package count is required")
    @Min(value = 1, message = "Package count must be at least 1")
    private Integer packageCount;

    private BigDecimal packageWeight;
    private String dimensions;
}
