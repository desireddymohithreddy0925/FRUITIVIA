package com.fruitivia.quotation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class QuotationItemDto {
    private UUID id;
    private UUID fruitId;
    private String fruitName;
    private UUID varietyId;
    private String varietyName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
