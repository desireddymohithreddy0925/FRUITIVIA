package com.fruitivia.audit.listener;

import com.fruitivia.common.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class AuditEventListener {

    // Listens to ALL DomainEvents but only after the transaction successfully commits
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = DomainEvent.class)
    public void handleDomainEventAudit(DomainEvent event) {
        log.info("AUDIT LOG: Event Type: {}, Event ID: {}, Timestamp: {}", 
                event.getEventType(), event.getEventId(), event.getEventTimestamp());
        
        // This is where we could save to a permanent append-only audit log table
        // We know the main transaction was successful since this is AFTER_COMMIT
    }
}
