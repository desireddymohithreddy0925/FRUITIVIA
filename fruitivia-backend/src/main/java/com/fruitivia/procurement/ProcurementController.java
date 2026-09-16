package com.fruitivia.procurement;

import com.fruitivia.procurement.dto.ProcurementOrderCreateRequest;
import com.fruitivia.procurement.dto.ProcurementOrderDto;
import com.fruitivia.procurement.dto.ProcurementStatusUpdateRequest;
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

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/procurement")
@RequiredArgsConstructor
@Tag(name = "Procurement API", description = "Internal procurement operations")
@SecurityRequirement(name = "bearerAuth")
public class ProcurementController {

    private final ProcurementService procurementService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Get all procurement orders")
    public ResponseEntity<Page<ProcurementOrderDto>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(procurementService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Get procurement order by ID")
    public ResponseEntity<ProcurementOrderDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(procurementService.getOrderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Create procurement order")
    public ResponseEntity<ProcurementOrderDto> createOrder(
            @Valid @RequestBody ProcurementOrderCreateRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "system";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(procurementService.createOrder(request, username));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Update procurement order status")
    public ResponseEntity<ProcurementOrderDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ProcurementStatusUpdateRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(procurementService.updateStatus(id, request, username));
    }
}
