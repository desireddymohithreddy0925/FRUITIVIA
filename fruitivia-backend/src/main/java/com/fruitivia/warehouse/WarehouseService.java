package com.fruitivia.warehouse;

import com.fruitivia.warehouse.dto.WarehouseCreateRequest;
import com.fruitivia.warehouse.dto.WarehouseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "reference", key = "'warehouses:' + (#type != null ? #type.name() : 'all') + ':' + #active + ':' + #minCapacity + ':' + #pageable")
    public Page<WarehouseDto> getWarehouses(WarehouseType type, Boolean active, Double minCapacity, Pageable pageable) {
        Specification<Warehouse> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (minCapacity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return warehouseRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "reference", key = "'warehouse:' + #id")
    public WarehouseDto getWarehouseById(UUID id) {
        return warehouseRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "reference", allEntries = true)
    public WarehouseDto createWarehouse(WarehouseCreateRequest request) {
        if (warehouseRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Warehouse name must be unique");
        }

        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .type(request.getType())
                .capacity(request.getCapacity())
                .minTemperature(request.getMinTemperature())
                .maxTemperature(request.getMaxTemperature())
                .minHumidity(request.getMinHumidity())
                .maxHumidity(request.getMaxHumidity())
                .build();
        warehouse.setActive(true);

        return mapToDto(warehouseRepository.save(warehouse));
    }

    private WarehouseDto mapToDto(Warehouse warehouse) {
        return WarehouseDto.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .type(warehouse.getType())
                .capacity(warehouse.getCapacity())
                .minTemperature(warehouse.getMinTemperature())
                .maxTemperature(warehouse.getMaxTemperature())
                .minHumidity(warehouse.getMinHumidity())
                .maxHumidity(warehouse.getMaxHumidity())
                .active(warehouse.isActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
}
