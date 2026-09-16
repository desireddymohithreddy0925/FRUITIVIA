package com.fruitivia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class FruitiviaApplicationTests {

    @Test
    void contextLoads() {
        // Test passes if the context loads successfully
    }

    @Test
    void testUtcTimeZoneIsSet() {
        assertEquals("UTC", TimeZone.getDefault().getID(), "System timezone should be strictly UTC");
    }
}
