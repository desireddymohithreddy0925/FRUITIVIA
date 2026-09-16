package com.fruitivia.coldchain;

import com.fruitivia.batch.FruitBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiryMonitorJob {

    private final FruitBatchRepository batchRepository;
    private final ShelfLifeService shelfLifeService;

    // Run every day at 1:00 AM UTC
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void monitorExpirations() {
        log.info("Starting ExpiryMonitorJob to detect near-expiry batches.");
        
        // Default threshold is 7 days as outlined in the plan.
        Instant thresholdDate = Instant.now().plus(7, ChronoUnit.DAYS);
        
        // Find all active batches whose expiration date is before threshold
        // And we'll just log or perhaps in the future we'd send notifications
        batchRepository.findAll().stream()
                .filter(b -> b.isActive() && b.getExpirationDate() != null)
                .filter(b -> b.getExpirationDate().isBefore(thresholdDate))
                .forEach(b -> {
                    ShelfLifeService.SpoilageRisk risk = shelfLifeService.evaluateSpoilageRisk(b);
                    log.warn("Batch {} (ID: {}) is near expiry. Risk: {}. Expiration Date: {}", 
                            b.getBatchNumber(), b.getId(), risk, b.getExpirationDate());
                            
                    // If it is EXPIRED, we might want to automatically change its state,
                    // but for now we just monitor and log as requested "near-expiry detection".
                    if (risk == ShelfLifeService.SpoilageRisk.EXPIRED) {
                        log.error("Batch {} has EXPIRED!", b.getBatchNumber());
                    }
                });
                
        log.info("Completed ExpiryMonitorJob.");
    }
}
