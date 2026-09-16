package com.fruitivia.admin.controllers;

import com.fruitivia.quotation.QuotationService;
import com.fruitivia.quotation.dto.AdminQuotationUpdate;
import com.fruitivia.quotation.dto.QuotationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/quotations")
@RequiredArgsConstructor
public class AdminQuotationController {

    private final QuotationService quotationService;

    @PutMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    public ResponseEntity<QuotationDto> updateAndSendQuotation(
            @PathVariable UUID id,
            @Valid @RequestBody AdminQuotationUpdate update) {
        return ResponseEntity.ok(quotationService.updateAndSendQuotation(id, update));
    }
}
