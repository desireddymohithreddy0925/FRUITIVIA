package com.fruitivia.supplier;

import com.fruitivia.supplier.dto.SupplierCreateRequest;
import com.fruitivia.supplier.dto.SupplierDto;
import com.fruitivia.supplier.dto.SupplierUpdateRequest;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management API", description = "Internal management of suppliers/farmers")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR')")
    @Operation(summary = "Search suppliers", description = "Dynamic search and pagination. Accessible by ADMIN, ENGINEER, QC_INSPECTOR")
    public ResponseEntity<Page<SupplierDto>> getSuppliers(
            @RequestParam Map<String, String> criteria,
            Pageable pageable) {
        return ResponseEntity.ok(supplierService.searchSuppliers(criteria, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR')")
    @Operation(summary = "Get supplier by ID", description = "Accessible by ADMIN, ENGINEER, QC_INSPECTOR")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Create supplier", description = "Accessible by ADMIN, ENGINEER")
    public ResponseEntity<SupplierDto> createSupplier(@Valid @RequestBody SupplierCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createSupplier(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Update supplier details", description = "Accessible by ADMIN, ENGINEER")
    public ResponseEntity<SupplierDto> updateSupplier(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierUpdateRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Deactivate supplier", description = "Accessible by ADMIN, ENGINEER")
    public ResponseEntity<Void> deactivateSupplier(@PathVariable UUID id) {
        supplierService.deactivateSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Activate supplier", description = "Accessible by ADMIN, ENGINEER")
    public ResponseEntity<Void> activateSupplier(@PathVariable UUID id) {
        supplierService.activateSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
