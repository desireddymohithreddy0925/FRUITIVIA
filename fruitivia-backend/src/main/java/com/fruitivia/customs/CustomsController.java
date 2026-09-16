package com.fruitivia.customs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customs")
@RequiredArgsConstructor
public class CustomsController {

    private final CustomsClearanceService clearanceService;

    @PostMapping("/orders/{orderId}/initiate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS')")
    public ResponseEntity<CustomsClearance> initiateClearance(@PathVariable UUID orderId) {
        return ResponseEntity.ok(clearanceService.initiateClearance(orderId));
    }

    @PostMapping("/clearance/{clearanceId}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS')")
    public ResponseEntity<CustomsClearance> submitDeclaration(
            @PathVariable UUID clearanceId,
            @RequestBody CustomsDeclaration declaration) {
        return ResponseEntity.ok(clearanceService.submitDeclaration(clearanceId, declaration));
    }

    @PatchMapping("/clearance/{clearanceId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS')")
    public ResponseEntity<CustomsClearance> updateStatus(
            @PathVariable UUID clearanceId,
            @RequestParam CustomsStatus status,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(clearanceService.updateStatus(clearanceId, status, remarks));
    }
}
