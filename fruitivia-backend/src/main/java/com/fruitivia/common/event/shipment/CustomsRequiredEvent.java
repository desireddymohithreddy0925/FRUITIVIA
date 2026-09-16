package com.fruitivia.common.event.shipment;

import com.fruitivia.common.event.DomainEvent;

import java.util.UUID;

public class CustomsRequiredEvent extends DomainEvent {
    private final UUID shipmentId;
    private final UUID orderId;

    public CustomsRequiredEvent(Object source, UUID shipmentId, UUID orderId) {
        super(source);
        this.shipmentId = shipmentId;
        this.orderId = orderId;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public UUID getOrderId() {
        return orderId;
    }
    
    @Override
    public String getEventType() {
        return "CUSTOMS_REQUIRED";
    }
}
