package com.fruitivia.payment.event;

import lombok.Getter;
import com.fruitivia.common.event.DomainEvent;

import java.util.UUID;

@Getter
public class PaymentSuccessEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID paymentId;

    public PaymentSuccessEvent(Object source, UUID orderId, UUID paymentId) {
        super(source);
        this.orderId = orderId;
        this.paymentId = paymentId;
    }
    
    @Override
    public String getEventType() {
        return "PaymentSuccessEvent";
    }
}
