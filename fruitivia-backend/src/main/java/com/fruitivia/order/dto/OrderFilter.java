package com.fruitivia.order.dto;

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
public class OrderFilter {
    private UUID buyerId;
    private String country;
    private String status;
    private Instant dateFrom;
    private Instant dateTo;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String currency;
}
