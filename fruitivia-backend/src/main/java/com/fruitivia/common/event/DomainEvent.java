package com.fruitivia.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent extends ApplicationEvent {
    
    private final UUID eventId;
    private final Instant eventTimestamp;

    public DomainEvent(Object source) {
        super(source);
        this.eventId = UUID.randomUUID();
        this.eventTimestamp = Instant.now();
    }

    public abstract String getEventType();
}
