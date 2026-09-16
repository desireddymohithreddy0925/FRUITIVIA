package com.fruitivia.quality;

import com.fruitivia.quality.dto.QualityInspectionCreateRequest;
import com.fruitivia.quality.dto.QualityInspectionDto;
import com.fruitivia.quality.dto.QualityInspectionStatusUpdateRequest;
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
@RequestMapping("/api/v1/quality")
@RequiredArgsConstructor
@Tag(name = "Quality Control API", description = "Operations related to batch quality inspection")
@SecurityRequirement(name = "bearerAuth")
public class QualityInspectionController {

    private final QualityInspectionService inspectionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR')")
    @Operation(summary = "Get all quality inspections")
    public ResponseEntity<Page<QualityInspectionDto>> getAllInspections(Pageable pageable) {
        return ResponseEntity.ok(inspectionService.getAllInspections(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR')")
    @Operation(summary = "Get inspection by ID")
    public ResponseEntity<QualityInspectionDto> getInspectionById(@PathVariable UUID id) {
        return ResponseEntity.ok(inspectionService.getInspectionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR')")
    @Operation(summary = "Create quality inspection")
    public ResponseEntity<QualityInspectionDto> createInspection(
            @Valid @RequestBody QualityInspectionCreateRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inspectionService.createInspection(request, principal.getName()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'QC_INSPECTOR')")
    @Operation(summary = "Update quality inspection status")
    public ResponseEntity<QualityInspectionDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody QualityInspectionStatusUpdateRequest request,
            Principal principal) {
        return ResponseEntity.ok(inspectionService.updateStatus(id, request, principal.getName()));
    }
}
