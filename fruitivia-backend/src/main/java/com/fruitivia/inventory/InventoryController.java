package com.fruitivia.inventory;

import com.fruitivia.inventory.dto.InventoryDto;
import com.fruitivia.inventory.dto.InventoryOperationRequest;
import com.fruitivia.inventory.dto.InventoryTransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "Operations related to inventory management")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS', 'QC_INSPECTOR')")
    @Operation(summary = "Get all inventory with filters")
    public ResponseEntity<org.springframework.data.domain.Page<InventoryDto>> getAllInventories(
            com.fruitivia.inventory.dto.InventoryFilter filter,
            @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventories(filter, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS', 'QC_INSPECTOR')")
    @Operation(summary = "Get inventory by ID")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS', 'QC_INSPECTOR')")
    @Operation(summary = "Get inventory by Batch ID")
    public ResponseEntity<InventoryDto> getInventoryByBatchId(@PathVariable UUID batchId) {
        return ResponseEntity.ok(inventoryService.getInventoryByBatchId(batchId));
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS', 'QC_INSPECTOR')")
    @Operation(summary = "Get inventory transaction history")
    public ResponseEntity<List<InventoryTransactionDto>> getTransactions(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.getTransactions(id));
    }

    @PostMapping("/batch/{batchId}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Receive new inventory for a batch")
    public ResponseEntity<InventoryDto> receiveBatch(
            @PathVariable UUID batchId,
            @RequestParam(required = false) UUID storageLocationId,
            @Valid @RequestBody InventoryOperationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.receiveBatch(batchId, storageLocationId, request));
    }

    @PostMapping("/{id}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Reserve inventory quantity")
    public ResponseEntity<InventoryDto> reserve(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryOperationRequest request) {
        return ResponseEntity.ok(inventoryService.reserve(id, request));
    }

    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Release reserved inventory quantity")
    public ResponseEntity<InventoryDto> release(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryOperationRequest request) {
        return ResponseEntity.ok(inventoryService.release(id, request));
    }

    @PostMapping("/{id}/pack")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Pack reserved inventory quantity")
    public ResponseEntity<InventoryDto> pack(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryOperationRequest request) {
        return ResponseEntity.ok(inventoryService.pack(id, request));
    }
}
