package com.fruitivia.payment.event;

import lombok.Getter;
import com.fruitivia.common.event.DomainEvent;
import java.util.UUID;

@Getter
public class PaymentFailedEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID paymentId;
    private final String reason;

    public PaymentFailedEvent(Object source, UUID orderId, UUID paymentId, String reason) {
        super(source);
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.reason = reason;
    }
    
    @Override
    public String getEventType() {
        return "PaymentFailedEvent";
    }
}
