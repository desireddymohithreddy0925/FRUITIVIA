package com.fruitivia.common.event;

public interface EventPublisher {
    void publish(DomainEvent event);
}
