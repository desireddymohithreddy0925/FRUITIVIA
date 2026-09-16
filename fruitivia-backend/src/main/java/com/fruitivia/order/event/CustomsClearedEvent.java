package com.fruitivia.order.event;

import com.fruitivia.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomsClearedEvent extends DomainEvent {
    private final UUID orderId;

    public CustomsClearedEvent(Object source, UUID orderId) {
        super(source);
        this.orderId = orderId;
    }

    @Override
    public String getEventType() {
        return "CustomsClearedEvent";
    }
}
