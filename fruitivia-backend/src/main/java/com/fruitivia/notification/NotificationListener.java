package com.fruitivia.notification;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.common.event.shipment.ShipmentDeliveredEvent;
import com.fruitivia.common.event.shipment.ShipmentDispatchedEvent;
import com.fruitivia.inventory.event.InventoryLowEvent;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.order.event.OrderPlacedEvent;
import com.fruitivia.payment.event.PaymentSuccessEvent;
import com.fruitivia.quotation.Quotation;
import com.fruitivia.quotation.QuotationRepository;
import com.fruitivia.quotation.event.QuotationAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final MailService mailService;
    private final OrderRepository orderRepository;
    private final QuotationRepository quotationRepository;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null) {
            Buyer buyer = order.getQuotation().getBuyer();
            String key = "order_placed_" + order.getId();
            mailService.sendEmailIdempotent(
                    key,
                    buyer.getContactEmail(),
                    "Order Placed Successfully",
                    "Your order " + order.getId() + " has been placed successfully.",
                    NotificationType.ORDER
            );
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null) {
            Buyer buyer = order.getQuotation().getBuyer();
            String key = "payment_success_" + event.getPaymentId();
            mailService.sendEmailIdempotent(
                    key,
                    buyer.getContactEmail(),
                    "Payment Received",
                    "Your payment for order " + order.getId() + " has been received successfully.",
                    NotificationType.PAYMENT
            );
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onShipmentDispatched(ShipmentDispatchedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null) {
            Buyer buyer = order.getQuotation().getBuyer();
            String key = "shipment_dispatched_" + event.getOrderId();
            mailService.sendEmailIdempotent(
                    key,
                    buyer.getContactEmail(),
                    "Shipment Dispatched",
                    "Your shipment for order " + event.getOrderId() + " has been dispatched.",
                    NotificationType.SHIPMENT
            );
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onShipmentDelivered(ShipmentDeliveredEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null) {
            Buyer buyer = order.getQuotation().getBuyer();
            String key = "shipment_delivered_" + event.getOrderId();
            mailService.sendEmailIdempotent(
                    key,
                    buyer.getContactEmail(),
                    "Shipment Delivered",
                    "Your shipment for order " + event.getOrderId() + " has been delivered successfully.",
                    NotificationType.DELIVERY
            );
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInventoryLow(InventoryLowEvent event) {
        String key = "inventory_low_" + event.getFruitId() + "_" + event.getEventTimestamp().toEpochMilli();
        mailService.sendEmailIdempotent(
                key,
                "admin@fruitivia.com",
                "Low Inventory Alert",
                "Low inventory detected for fruit ID: " + event.getFruitId(),
                NotificationType.LOW_INVENTORY
        );
    }
}
