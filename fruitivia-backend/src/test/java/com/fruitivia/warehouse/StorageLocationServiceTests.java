package com.fruitivia.warehouse;

import com.fruitivia.fruit.FruitVariety;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StorageLocationServiceTests {

    @Mock
    private StorageLocationRepository storageLocationRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private StorageLocationService storageLocationService;

    @Test
    void testCompatibility_GeneralWarehouseRejectsTempSensitiveVariety() {
        Warehouse warehouse = Warehouse.builder().type(WarehouseType.GENERAL).build();
        FruitVariety variety = FruitVariety.builder().minStorageTemperature(2.0).maxStorageTemperature(6.0).build();

        assertThrows(IllegalArgumentException.class, 
            () -> storageLocationService.validateFruitVarietyCompatibility(warehouse, variety));
    }

    @Test
    void testCompatibility_ColdStorageMismatchedTemp() {
        Warehouse warehouse = Warehouse.builder()
                .type(WarehouseType.COLD_STORAGE)
                .minTemperature(10.0)
                .maxTemperature(15.0)
                .build();
        FruitVariety variety = FruitVariety.builder().minStorageTemperature(2.0).maxStorageTemperature(6.0).build();

        assertThrows(IllegalArgumentException.class,
                () -> storageLocationService.validateFruitVarietyCompatibility(warehouse, variety));
    }

    @Test
    void testCompatibility_ValidTempMatch() {
        Warehouse warehouse = Warehouse.builder()
                .type(WarehouseType.COLD_STORAGE)
                .minTemperature(0.0)
                .maxTemperature(5.0)
                .build();
        FruitVariety variety = FruitVariety.builder().minStorageTemperature(2.0).maxStorageTemperature(6.0).build();

        assertDoesNotThrow(() -> storageLocationService.validateFruitVarietyCompatibility(warehouse, variety));
    }
}
