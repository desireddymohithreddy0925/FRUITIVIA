package com.fruitivia.notification.listener;

import com.fruitivia.order.event.OrderPlacedEvent;
import com.fruitivia.payment.event.PaymentSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventListener {

    @Async("taskExecutor")
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Sending order confirmation notification for Order ID: {}", event.getOrderId());
        // Simulating email/SMS logic
        try {
            Thread.sleep(500); // Simulate delay
            log.debug("Notification sent for Order ID: {}", event.getOrderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async("taskExecutor")
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Sending payment receipt for Payment ID: {} associated with Order ID: {}", event.getPaymentId(), event.getOrderId());
        // Simulating email/SMS logic
    }
}
