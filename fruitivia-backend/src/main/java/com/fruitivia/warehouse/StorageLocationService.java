package com.fruitivia.warehouse;

import com.fruitivia.fruit.FruitVariety;
import com.fruitivia.warehouse.dto.StorageLocationCreateRequest;
import com.fruitivia.warehouse.dto.StorageLocationDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "reference", key = "'storage-locations:warehouse:' + #warehouseId")
    public List<StorageLocationDto> getLocationsForWarehouse(UUID warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new EntityNotFoundException("Warehouse not found");
        }
        return storageLocationRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "reference", key = "'storage-locations:warehouse:' + #warehouseId")
    public StorageLocationDto createLocation(UUID warehouseId, StorageLocationCreateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        if (!warehouse.isActive()) {
            throw new IllegalArgumentException("Cannot add locations to an inactive warehouse");
        }

        StorageLocation location = StorageLocation.builder()
                .locationCode(request.getLocationCode())
                .warehouse(warehouse)
                .capacity(request.getCapacity())
                .currentUsage(BigDecimal.ZERO)
                .build();
        location.setActive(true);

        return mapToDto(storageLocationRepository.save(location));
    }

    public void validateFruitVarietyCompatibility(Warehouse warehouse, FruitVariety variety) {
        if (warehouse.getType() == WarehouseType.GENERAL && variety.getMinStorageTemperature() != null) {
            throw new IllegalArgumentException("General warehouse cannot store temperature sensitive varieties");
        }
        
        if (variety.getMinStorageTemperature() != null && warehouse.getMinTemperature() != null) {
            if (warehouse.getMinTemperature() > variety.getMaxStorageTemperature() || 
                warehouse.getMaxTemperature() < variety.getMinStorageTemperature()) {
                throw new IllegalArgumentException("Warehouse temperature range is incompatible with the variety's requirements");
            }
        }
    }

    private StorageLocationDto mapToDto(StorageLocation location) {
        return StorageLocationDto.builder()
                .id(location.getId())
                .locationCode(location.getLocationCode())
                .warehouseId(location.getWarehouse().getId())
                .warehouseName(location.getWarehouse().getName())
                .capacity(location.getCapacity())
                .currentUsage(location.getCurrentUsage())
                .active(location.isActive())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
}
