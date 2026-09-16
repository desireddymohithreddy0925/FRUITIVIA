package com.fruitivia.quotation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class QuotationItemRequest {
    @NotNull
    private UUID fruitId;
    
    @NotNull
    private UUID varietyId;
    
    @NotNull
    @Positive
    private BigDecimal quantity;
}
