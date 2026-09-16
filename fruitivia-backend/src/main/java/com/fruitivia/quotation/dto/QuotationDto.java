package com.fruitivia.quotation.dto;

import com.fruitivia.quotation.QuotationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuotationDto {
    private UUID id;
    private UUID buyerId;
    private QuotationStatus status;
    private String currencyCode;
    
    private BigDecimal packagingCost;
    private BigDecimal transportationCost;
    private BigDecimal exportHandlingCost;
    private BigDecimal shippingCost;
    private BigDecimal insuranceCost;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    
    private String paymentTerms;
    private OffsetDateTime validityDate;
    
    private List<QuotationItemDto> items;
    
    private Instant createdAt;
    private Instant updatedAt;
}
