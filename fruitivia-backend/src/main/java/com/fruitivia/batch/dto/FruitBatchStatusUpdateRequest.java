package com.fruitivia.batch.dto;

import com.fruitivia.batch.BatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FruitBatchStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private BatchStatus status;

    private String comments;
}
