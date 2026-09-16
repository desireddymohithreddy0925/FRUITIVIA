package com.fruitivia.shipment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentStateMachineService shipmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS')")
    public ResponseEntity<org.springframework.data.domain.Page<Shipment>> getAllShipments(
            com.fruitivia.shipment.dto.ShipmentFilter filter,
            @org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(shipmentService.getAllShipments(filter, pageable));
    }

    @PostMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS')")
    public ResponseEntity<Shipment> createShipment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(shipmentService.createShipment(orderId));
    }

    @PatchMapping("/{shipmentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS')")
    public ResponseEntity<Shipment> updateStatus(
            @PathVariable UUID shipmentId,
            @RequestParam ShipmentStatus status,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(shipmentService.transitionState(shipmentId, status, remarks));
    }
}
