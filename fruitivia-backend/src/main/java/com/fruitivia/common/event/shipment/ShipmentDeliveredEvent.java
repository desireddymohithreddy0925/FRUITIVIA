package com.fruitivia.common.event.shipment;

import com.fruitivia.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ShipmentDeliveredEvent extends DomainEvent {
    private final UUID orderId;

    public ShipmentDeliveredEvent(Object source, UUID orderId) {
        super(source);
        this.orderId = orderId;
    }

    @Override
    public String getEventType() {
        return "ShipmentDeliveredEvent";
    }
}
