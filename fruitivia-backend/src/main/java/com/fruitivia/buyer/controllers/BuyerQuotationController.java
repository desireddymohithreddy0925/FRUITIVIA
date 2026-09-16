package com.fruitivia.buyer.controllers;

import com.fruitivia.quotation.QuotationService;
import com.fruitivia.quotation.QuotationStatus;
import com.fruitivia.quotation.dto.QuotationDto;
import com.fruitivia.quotation.dto.QuotationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotations")
@RequiredArgsConstructor
public class BuyerQuotationController {

    private final QuotationService quotationService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<QuotationDto> requestQuotation(@Valid @RequestBody QuotationRequest request) {
        return ResponseEntity.ok(quotationService.requestQuotation(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Page<QuotationDto>> getMyQuotations(
            @RequestParam(required = false) QuotationStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(quotationService.getMyQuotations(status, pageable));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<QuotationDto> getMyQuotation(@PathVariable UUID id) {
        return ResponseEntity.ok(quotationService.getMyQuotation(id));
    }

    @PostMapping("/me/{id}/accept")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<QuotationDto> acceptQuotation(@PathVariable UUID id) {
        return ResponseEntity.ok(quotationService.acceptQuotation(id));
    }

    @PostMapping("/me/{id}/reject")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<QuotationDto> rejectQuotation(@PathVariable UUID id) {
        return ResponseEntity.ok(quotationService.rejectQuotation(id));
    }
}
