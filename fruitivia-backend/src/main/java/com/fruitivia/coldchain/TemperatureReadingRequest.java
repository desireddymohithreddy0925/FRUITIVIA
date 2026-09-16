package com.fruitivia.coldchain;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TemperatureReadingRequest(
        @NotNull UUID batchId,
        @NotNull UUID warehouseId,
        @NotNull BigDecimal temperature,
        BigDecimal humidity,
        @NotNull Instant timestamp,
        @NotNull ReadingSource source,
        String notes
) {
}
