package com.fruitivia.buyer.controllers;

import com.fruitivia.payment.Payment;
import com.fruitivia.payment.PaymentService;
import com.fruitivia.payment.provider.PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buyer/payments")
@RequiredArgsConstructor
@Tag(name = "Buyer Payment API", description = "Endpoints for buyers to process payments")
@PreAuthorize("hasRole('BUYER')")
public class BuyerPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/order/{orderId}")
    @Operation(summary = "Process a payment for an order")
    public ResponseEntity<Payment> processPayment(@PathVariable UUID orderId, @RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(orderId, request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Object>> getMyPayments() {
        // Mock implementation for the existing endpoint
        return ResponseEntity.ok(Collections.emptyList());
    }
}
