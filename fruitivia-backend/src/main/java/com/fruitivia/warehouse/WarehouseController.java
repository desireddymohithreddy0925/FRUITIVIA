package com.fruitivia.warehouse;

import com.fruitivia.warehouse.dto.WarehouseCreateRequest;
import com.fruitivia.warehouse.dto.WarehouseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouse API", description = "Operations related to warehouses and cold storage")
@SecurityRequirement(name = "bearerAuth")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Get warehouses with filters")
    public ResponseEntity<Page<WarehouseDto>> getWarehouses(
            @RequestParam(required = false) WarehouseType type,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Double minCapacity,
            Pageable pageable) {
        return ResponseEntity.ok(warehouseService.getWarehouses(type, active, minCapacity, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<WarehouseDto> getWarehouseById(@PathVariable UUID id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Create warehouse")
    public ResponseEntity<WarehouseDto> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(request));
    }
}
