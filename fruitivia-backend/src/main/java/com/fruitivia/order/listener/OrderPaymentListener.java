package com.fruitivia.order.listener;

import com.fruitivia.order.OrderStateMachineService;
import com.fruitivia.order.OrderStatus;
import com.fruitivia.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentListener {

    private final OrderStateMachineService orderStateMachineService;

    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Received PaymentSuccessEvent for orderId: {}, paymentId: {}. Transitioning to PAID.", event.getOrderId(), event.getPaymentId());
        
        try {
            orderStateMachineService.systemTransitionState(
                    event.getOrderId(), 
                    OrderStatus.PAID, 
                    "System transitioned to PAID due to successful payment " + event.getPaymentId()
            );
        } catch (Exception e) {
            log.error("Failed to automatically transition order {} to PAID upon successful payment: {}", event.getOrderId(), e.getMessage());
        }
    }
}
