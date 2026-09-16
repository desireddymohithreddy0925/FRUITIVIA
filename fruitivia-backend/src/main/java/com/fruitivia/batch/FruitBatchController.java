package com.fruitivia.batch;

import com.fruitivia.batch.dto.FruitBatchCreateRequest;
import com.fruitivia.batch.dto.FruitBatchDto;
import com.fruitivia.batch.dto.FruitBatchStatusUpdateRequest;
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
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
@Tag(name = "Fruit Batch API", description = "Operations related to fruit batch traceability")
@SecurityRequirement(name = "bearerAuth")
public class FruitBatchController {

    private final FruitBatchService batchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR', 'LOGISTICS')")
    @Operation(summary = "Get all fruit batches")
    public ResponseEntity<Page<FruitBatchDto>> getAllBatches(
            com.fruitivia.batch.dto.FruitBatchFilter filter,
            @org.springframework.data.web.PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(batchService.getAllBatches(filter, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR', 'LOGISTICS')")
    @Operation(summary = "Get fruit batch by ID")
    public ResponseEntity<FruitBatchDto> getBatchById(@PathVariable UUID id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(summary = "Create fruit batch")
    public ResponseEntity<FruitBatchDto> createBatch(
            @Valid @RequestBody FruitBatchCreateRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(batchService.createBatch(request, principal.getName()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @Operation(summary = "Update fruit batch status")
    public ResponseEntity<FruitBatchDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody FruitBatchStatusUpdateRequest request,
            Principal principal) {
        return ResponseEntity.ok(batchService.updateStatus(id, request, principal.getName()));
    }
}
