package com.fruitivia.coldchain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Abstraction for future IoT sensor integration.
 * Will allow pulling temperature and humidity logs from external IoT platform APIs.
 */
public interface SensorIntegrationProvider {
    
    /**
     * Retrieve recent readings for a specific warehouse.
     */
    List<SensorReadingDto> fetchRecentReadings(UUID warehouseId, Instant since);

    record SensorReadingDto(
        BigDecimal temperature,
        BigDecimal humidity,
        Instant timestamp
    ) {}
}
