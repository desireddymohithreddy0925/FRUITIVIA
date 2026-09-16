package com.fruitivia.procurement.dto;

import com.fruitivia.procurement.ProcurementStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private ProcurementStatus status;

    private String comments;
}
