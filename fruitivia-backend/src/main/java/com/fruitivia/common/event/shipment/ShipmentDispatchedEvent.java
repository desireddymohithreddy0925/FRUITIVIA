package com.fruitivia.common.event.shipment;

import com.fruitivia.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ShipmentDispatchedEvent extends DomainEvent {
    private final UUID orderId;

    public ShipmentDispatchedEvent(Object source, UUID orderId) {
        super(source);
        this.orderId = orderId;
    }

    @Override
    public String getEventType() {
        return "ShipmentDispatchedEvent";
    }
}
