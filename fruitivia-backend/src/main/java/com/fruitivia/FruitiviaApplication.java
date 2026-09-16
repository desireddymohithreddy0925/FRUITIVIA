package com.fruitivia;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

import org.springframework.cache.annotation.EnableCaching;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAsync
@EnableAdminServer
@EnableScheduling
@EnableCaching
public class FruitiviaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FruitiviaApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Enforce UTC timezone across the entire application
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
