package com.fruitivia.packaging.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingCreateRequest {
    
    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Packing date is required")
    private OffsetDateTime packingDate;

    private String packedBy;
    private String notes;

    @NotEmpty(message = "Packaging items are required")
    @Valid
    private List<PackagingItemRequest> items;
}
