package com.fruitivia.admin.controllers;

import com.fruitivia.order.OrderService;
import com.fruitivia.order.OrderStateMachineService;
import com.fruitivia.order.OrderStatus;
import com.fruitivia.order.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Order API", description = "Endpoints for admins and engineers to manage orders")
@PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderStateMachineService orderStateMachineService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<org.springframework.data.domain.Page<OrderDto>> getAllOrders(
            com.fruitivia.order.dto.OrderFilter filter,
            @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/{id}/transition")
    @Operation(summary = "Transition order state")
    public ResponseEntity<Void> transitionOrder(@PathVariable UUID id, 
                                                @RequestParam OrderStatus targetStatus, 
                                                @RequestParam(required = false) String notes) {
        orderStateMachineService.transitionState(id, targetStatus, notes);
        return ResponseEntity.ok().build();
    }
}
