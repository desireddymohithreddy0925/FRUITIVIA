package com.fruitivia.order.event;

import com.fruitivia.common.event.DomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class OrderPlacedEvent extends DomainEvent {
    
    private final UUID orderId;

    public OrderPlacedEvent(Object source, UUID orderId) {
        super(source);
        this.orderId = orderId;
    }

    @Override
    public String getEventType() {
        return "OrderPlacedEvent";
    }
}
