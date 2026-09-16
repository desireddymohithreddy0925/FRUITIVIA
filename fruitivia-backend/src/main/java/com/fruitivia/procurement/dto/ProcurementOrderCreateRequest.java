package com.fruitivia.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementOrderCreateRequest {

    @NotNull(message = "Supplier ID is required")
    private UUID supplierId;

    @NotNull(message = "Purchase date is required")
    private Instant purchaseDate;

    private Instant harvestDate;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    private String currency; // Optional, defaults to INR

    private String notes;

    @NotEmpty(message = "Procurement items cannot be empty")
    @Valid
    private List<ProcurementItemCreateRequest> items;
}
