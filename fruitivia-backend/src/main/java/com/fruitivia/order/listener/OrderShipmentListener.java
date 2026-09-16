package com.fruitivia.order.listener;

import com.fruitivia.common.event.shipment.CustomsRequiredEvent;
import com.fruitivia.common.event.shipment.ShipmentDeliveredEvent;
import com.fruitivia.common.event.shipment.ShipmentDispatchedEvent;
import com.fruitivia.order.OrderStateMachineService;
import com.fruitivia.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderShipmentListener {

    private final OrderStateMachineService orderStateMachineService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onShipmentDispatched(ShipmentDispatchedEvent event) {
        log.info("Handling ShipmentDispatchedEvent for Order: {}", event.getOrderId());
        orderStateMachineService.systemTransitionState(event.getOrderId(), OrderStatus.DISPATCHED, "Shipment dispatched");
        // We can also transition to IN_TRANSIT immediately if needed, or wait for next shipment status
        orderStateMachineService.systemTransitionState(event.getOrderId(), OrderStatus.IN_TRANSIT, "Shipment in transit");
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCustomsRequired(CustomsRequiredEvent event) {
        log.info("Handling CustomsRequiredEvent for Order: {}", event.getOrderId());
        orderStateMachineService.systemTransitionState(event.getOrderId(), OrderStatus.CUSTOMS, "Customs clearance required");
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onShipmentDelivered(ShipmentDeliveredEvent event) {
        log.info("Handling ShipmentDeliveredEvent for Order: {}", event.getOrderId());
        orderStateMachineService.systemTransitionState(event.getOrderId(), OrderStatus.DELIVERED, "Shipment delivered");
    }
}
