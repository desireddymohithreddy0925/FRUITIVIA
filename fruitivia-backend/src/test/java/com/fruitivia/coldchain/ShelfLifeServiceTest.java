package com.fruitivia.coldchain;

import com.fruitivia.batch.FruitBatch;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ShelfLifeServiceTest {

    private final ShelfLifeService shelfLifeService = new ShelfLifeService();

    @Test
    void testIsExpired() {
        FruitBatch batch = new FruitBatch();
        
        // Not expired
        batch.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        assertFalse(shelfLifeService.isExpired(batch));
        
        // Expired
        batch.setExpirationDate(Instant.now().minus(1, ChronoUnit.DAYS));
        assertTrue(shelfLifeService.isExpired(batch));
        
        // Null expiration date (handled gracefully, assumes not expired)
        batch.setExpirationDate(null);
        assertFalse(shelfLifeService.isExpired(batch));
    }
    
    @Test
    void testGetRemainingShelfLife() {
        FruitBatch batch = new FruitBatch();
        
        batch.setExpirationDate(Instant.now().plus(2, ChronoUnit.DAYS));
        Duration remaining = shelfLifeService.getRemainingShelfLife(batch);
        assertTrue(remaining.toDays() >= 1 && remaining.toDays() <= 2);
        
        batch.setExpirationDate(Instant.now().minus(1, ChronoUnit.DAYS));
        assertEquals(Duration.ZERO, shelfLifeService.getRemainingShelfLife(batch));
    }

    @Test
    void testEvaluateSpoilageRisk() {
        FruitBatch batch = new FruitBatch();
        
        // > 7 days = LOW
        batch.setExpirationDate(Instant.now().plus(10, ChronoUnit.DAYS));
        assertEquals(ShelfLifeService.SpoilageRisk.LOW, shelfLifeService.evaluateSpoilageRisk(batch));
        
        // 3-7 days = MEDIUM
        batch.setExpirationDate(Instant.now().plus(5, ChronoUnit.DAYS));
        assertEquals(ShelfLifeService.SpoilageRisk.MEDIUM, shelfLifeService.evaluateSpoilageRisk(batch));
        
        // < 3 days = HIGH
        batch.setExpirationDate(Instant.now().plus(2, ChronoUnit.DAYS));
        assertEquals(ShelfLifeService.SpoilageRisk.HIGH, shelfLifeService.evaluateSpoilageRisk(batch));
        
        // Expired
        batch.setExpirationDate(Instant.now().minus(1, ChronoUnit.DAYS));
        assertEquals(ShelfLifeService.SpoilageRisk.EXPIRED, shelfLifeService.evaluateSpoilageRisk(batch));
    }
}
