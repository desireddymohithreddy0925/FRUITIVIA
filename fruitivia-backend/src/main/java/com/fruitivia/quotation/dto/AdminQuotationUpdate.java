package com.fruitivia.quotation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class AdminQuotationUpdate {
    @NotBlank
    private String currencyCode;

    @NotNull
    @PositiveOrZero
    private BigDecimal packagingCost;

    @NotNull
    @PositiveOrZero
    private BigDecimal transportationCost;

    @NotNull
    @PositiveOrZero
    private BigDecimal exportHandlingCost;

    @NotNull
    @PositiveOrZero
    private BigDecimal shippingCost;

    @NotNull
    @PositiveOrZero
    private BigDecimal insuranceCost;

    @NotNull
    @PositiveOrZero
    private BigDecimal discount;

    @NotNull
    @PositiveOrZero
    private BigDecimal taxAmount;

    @NotBlank
    private String paymentTerms;

    @NotNull
    @Future
    private OffsetDateTime validityDate;

    // Map of QuotationItemId to unit price
    @NotNull
    private Map<UUID, @NotNull @PositiveOrZero BigDecimal> itemPrices;
}
