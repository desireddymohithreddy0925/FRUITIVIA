package com.fruitivia.buyer.controllers;

import com.fruitivia.order.OrderService;
import com.fruitivia.order.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Buyer Order API", description = "Endpoints for buyers to manage their orders")
public class BuyerOrderController {

    private final OrderService orderService;

    @PostMapping("/from-quotation/{quotationId}")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Create an order from an accepted quotation")
    public ResponseEntity<OrderDto> createOrder(@PathVariable UUID quotationId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrderFromQuotation(quotationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Get order details")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<Object>> getMyOrders() {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
