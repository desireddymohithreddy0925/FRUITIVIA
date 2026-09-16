package com.fruitivia.coldchain;

import com.fruitivia.batch.FruitBatch;
import com.fruitivia.batch.FruitBatchRepository;
import jakarta.persistence.EntityNotFoundException;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.warehouse.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColdChainService {

    private final TemperatureReadingRepository readingRepository;
    private final ColdChainEventRepository eventRepository;
    private final FruitBatchRepository batchRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public TemperatureReading recordReading(TemperatureReadingRequest request) {
        FruitBatch batch = batchRepository.findById(request.batchId())
                .orElseThrow(() -> new EntityNotFoundException("Batch not found"));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        TemperatureReading reading = TemperatureReading.builder()
                .batch(batch)
                .warehouse(warehouse)
                .temperature(request.temperature())
                .humidity(request.humidity())
                .timestamp(request.timestamp())
                .source(request.source())
                .notes(request.notes())
                .build();

        reading = readingRepository.save(reading);

        // Check for excursion
        checkTemperatureExcursion(reading, warehouse, batch);

        return reading;
    }

    private void checkTemperatureExcursion(TemperatureReading reading, Warehouse warehouse, FruitBatch batch) {
        if (warehouse.getMinTemperature() != null && reading.getTemperature().compareTo(BigDecimal.valueOf(warehouse.getMinTemperature())) < 0) {
            createEvent(batch, warehouse, ColdChainEventType.TEMPERATURE_EXCURSION, reading.getTimestamp(),
                    "Temperature dropped below minimum threshold of " + warehouse.getMinTemperature());
        } else if (warehouse.getMaxTemperature() != null && reading.getTemperature().compareTo(BigDecimal.valueOf(warehouse.getMaxTemperature())) > 0) {
            createEvent(batch, warehouse, ColdChainEventType.TEMPERATURE_EXCURSION, reading.getTimestamp(),
                    "Temperature exceeded maximum threshold of " + warehouse.getMaxTemperature());
        }
    }

    @Transactional
    public ColdChainEvent createEvent(FruitBatch batch, Warehouse warehouse, ColdChainEventType type, java.time.Instant timestamp, String notes) {
        ColdChainEvent event = ColdChainEvent.builder()
                .batch(batch)
                .warehouse(warehouse)
                .eventType(type)
                .eventTimestamp(timestamp)
                .notes(notes)
                .build();
        return eventRepository.save(event);
    }
    
    public List<ColdChainEvent> getEventsForBatch(UUID batchId) {
        return eventRepository.findByBatchId(batchId);
    }
    
    public List<TemperatureReading> getReadingsForBatch(UUID batchId) {
        return readingRepository.findByBatchId(batchId);
    }
}
