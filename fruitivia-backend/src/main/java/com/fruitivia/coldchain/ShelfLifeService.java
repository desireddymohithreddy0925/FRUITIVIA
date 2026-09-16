package com.fruitivia.coldchain;

import com.fruitivia.batch.FruitBatch;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class ShelfLifeService {

    /**
     * Returns true if the batch is already expired.
     */
    public boolean isExpired(FruitBatch batch) {
        if (batch.getExpirationDate() == null) {
            return false;
        }
        return Instant.now().isAfter(batch.getExpirationDate());
    }

    /**
     * Calculates the remaining shelf life as a java.time.Duration.
     * Returns Duration.ZERO if already expired.
     */
    public Duration getRemainingShelfLife(FruitBatch batch) {
        if (isExpired(batch)) {
            return Duration.ZERO;
        }
        return Duration.between(Instant.now(), batch.getExpirationDate());
    }

    /**
     * Spoilage risk is evaluated based on the remaining shelf life.
     * HIGH: < 3 days left
     * MEDIUM: 3-7 days left
     * LOW: > 7 days left
     */
    public SpoilageRisk evaluateSpoilageRisk(FruitBatch batch) {
        if (isExpired(batch)) {
            return SpoilageRisk.EXPIRED;
        }
        long daysLeft = getRemainingShelfLife(batch).toDays();
        if (daysLeft < 3) {
            return SpoilageRisk.HIGH;
        } else if (daysLeft <= 7) {
            return SpoilageRisk.MEDIUM;
        } else {
            return SpoilageRisk.LOW;
        }
    }

    public enum SpoilageRisk {
        LOW,
        MEDIUM,
        HIGH,
        EXPIRED
    }
}
