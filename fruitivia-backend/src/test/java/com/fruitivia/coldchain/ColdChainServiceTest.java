package com.fruitivia.coldchain;

import com.fruitivia.batch.FruitBatch;
import com.fruitivia.batch.FruitBatchRepository;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.warehouse.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ColdChainServiceTest {

    @Mock
    private TemperatureReadingRepository readingRepository;

    @Mock
    private ColdChainEventRepository eventRepository;

    @Mock
    private FruitBatchRepository batchRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private ColdChainService coldChainService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void recordReading_NoExcursion() {
        UUID batchId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        FruitBatch batch = new FruitBatch();
        batch.setId(batchId);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setMinTemperature(0.0);
        warehouse.setMaxTemperature(10.0);

        TemperatureReadingRequest request = new TemperatureReadingRequest(
                batchId, warehouseId, BigDecimal.valueOf(5), BigDecimal.valueOf(80),
                Instant.now(), ReadingSource.IOT_SENSOR, "Test reading"
        );

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(readingRepository.save(any(TemperatureReading.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TemperatureReading reading = coldChainService.recordReading(request);

        assertNotNull(reading);
        assertEquals(BigDecimal.valueOf(5), reading.getTemperature());
        
        // Ensure no event was created since 5 is between 0 and 10
        verify(eventRepository, never()).save(any(ColdChainEvent.class));
    }

    @Test
    void recordReading_TemperatureTooHigh_CreatesExcursionEvent() {
        UUID batchId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        FruitBatch batch = new FruitBatch();
        batch.setId(batchId);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setMinTemperature(0.0);
        warehouse.setMaxTemperature(10.0);

        TemperatureReadingRequest request = new TemperatureReadingRequest(
                batchId, warehouseId, BigDecimal.valueOf(15), BigDecimal.valueOf(80),
                Instant.now(), ReadingSource.IOT_SENSOR, "Test reading"
        );

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(readingRepository.save(any(TemperatureReading.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any(ColdChainEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TemperatureReading reading = coldChainService.recordReading(request);

        assertNotNull(reading);
        
        ArgumentCaptor<ColdChainEvent> eventCaptor = ArgumentCaptor.forClass(ColdChainEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        
        ColdChainEvent event = eventCaptor.getValue();
        assertEquals(ColdChainEventType.TEMPERATURE_EXCURSION, event.getEventType());
        assertTrue(event.getNotes().contains("exceeded maximum threshold"));
    }
    
    @Test
    void recordReading_TemperatureTooLow_CreatesExcursionEvent() {
        UUID batchId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        FruitBatch batch = new FruitBatch();
        batch.setId(batchId);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setMinTemperature(5.0);
        warehouse.setMaxTemperature(10.0);

        TemperatureReadingRequest request = new TemperatureReadingRequest(
                batchId, warehouseId, BigDecimal.valueOf(2), BigDecimal.valueOf(80),
                Instant.now(), ReadingSource.IOT_SENSOR, "Test reading"
        );

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(readingRepository.save(any(TemperatureReading.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any(ColdChainEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TemperatureReading reading = coldChainService.recordReading(request);

        assertNotNull(reading);
        
        ArgumentCaptor<ColdChainEvent> eventCaptor = ArgumentCaptor.forClass(ColdChainEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        
        ColdChainEvent event = eventCaptor.getValue();
        assertEquals(ColdChainEventType.TEMPERATURE_EXCURSION, event.getEventType());
        assertTrue(event.getNotes().contains("dropped below minimum threshold"));
    }
}
