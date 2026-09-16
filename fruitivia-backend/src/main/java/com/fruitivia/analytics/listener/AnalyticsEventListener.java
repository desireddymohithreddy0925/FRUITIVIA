package com.fruitivia.analytics.listener;

import com.fruitivia.common.event.shipment.ShipmentDeliveredEvent;
import com.fruitivia.quotation.event.QuotationAcceptedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnalyticsEventListener {

    @Async("taskExecutor")
    @EventListener
    public void handleQuotationAccepted(QuotationAcceptedEvent event) {
        log.info("Recording analytics for QuotationAcceptedEvent: Quotation ID: {}", event.getQuotationId());
        // Simulating data warehouse update
    }

    @Async("taskExecutor")
    @EventListener
    public void handleShipmentDelivered(ShipmentDeliveredEvent event) {
        log.info("Updating delivery performance metrics for Order ID: {}", event.getOrderId());
        // Simulating analytics update
    }
}
