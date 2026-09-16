package com.fruitivia.packaging.dto;

import com.fruitivia.packaging.PackagingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private PackagingStatus status;
    
    private String notes;
}
