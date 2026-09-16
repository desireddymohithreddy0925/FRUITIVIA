package com.fruitivia.quality.dto;

import com.fruitivia.quality.InspectionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private InspectionStatus status;

    private String comments;
}
