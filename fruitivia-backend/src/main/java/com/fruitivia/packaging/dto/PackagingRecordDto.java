package com.fruitivia.packaging.dto;

import com.fruitivia.packaging.PackagingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingRecordDto {
    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private PackagingStatus status;
    private OffsetDateTime packingDate;
    private String packedBy;
    private String notes;
    private List<PackagingItemDto> items;
    private Instant createdAt;
    private Instant updatedAt;
}
