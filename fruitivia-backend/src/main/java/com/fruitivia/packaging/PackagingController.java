package com.fruitivia.packaging;

import com.fruitivia.packaging.dto.PackagingCreateRequest;
import com.fruitivia.packaging.dto.PackagingRecordDto;
import com.fruitivia.packaging.dto.PackagingStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/packaging")
@RequiredArgsConstructor
public class PackagingController {

    private final PackagingService packagingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    @ResponseStatus(HttpStatus.CREATED)
    public PackagingRecordDto createPackagingRecord(@Valid @RequestBody PackagingCreateRequest request) {
        return packagingService.createPackagingRecord(request);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    public PackagingRecordDto updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PackagingStatusUpdateRequest request) {
        return packagingService.updateStatus(id, request);
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS', 'BUYER')")
    public List<PackagingRecordDto> getPackagingRecordsByOrder(@PathVariable UUID orderId) {
        // IDOR checking would ideally be handled in service to ensure Buyer can only see their own order's packaging
        return packagingService.getPackagingRecordsByOrder(orderId);
    }
}
