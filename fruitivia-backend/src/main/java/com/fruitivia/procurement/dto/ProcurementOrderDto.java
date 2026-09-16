package com.fruitivia.procurement.dto;

import com.fruitivia.procurement.PaymentStatus;
import com.fruitivia.procurement.ProcurementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementOrderDto {
    private UUID id;
    private String orderNumber;
    private UUID supplierId;
    private String supplierName;
    private Instant purchaseDate;
    private Instant harvestDate;
    private UUID warehouseId;
    private String warehouseName;
    private ProcurementStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String currency;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ProcurementItemDto> items;
}
