package com.fruitivia.order.dto;

import com.fruitivia.order.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID id;
    private String orderNumber;
    private UUID quotationId;
    private UUID buyerId;
    private OrderStatus status;
    private String currencyCode;
    private BigDecimal totalAmount;
    private List<OrderItemDto> items;
    private Instant createdAt;
}
