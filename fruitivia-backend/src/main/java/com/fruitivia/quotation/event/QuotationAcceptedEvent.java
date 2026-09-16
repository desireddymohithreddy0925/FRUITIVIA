package com.fruitivia.quotation.event;

import com.fruitivia.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class QuotationAcceptedEvent extends DomainEvent {
    private final UUID quotationId;

    public QuotationAcceptedEvent(Object source, UUID quotationId) {
        super(source);
        this.quotationId = quotationId;
    }

    @Override
    public String getEventType() {
        return "QuotationAcceptedEvent";
    }
}
