package com.fruitivia.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRevisionListenerTest {

    private AuditRevisionListener listener;

    @BeforeEach
    void setUp() {
        listener = new AuditRevisionListener();
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void newRevision_WithAuthenticatedUser_SetsActorAndCorrelationId() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("auditor@example.com", "password", java.util.Collections.emptyList())
        );
        MDC.put("correlationId", "corr-456");

        AuditRevisionEntity entity = new AuditRevisionEntity();

        listener.newRevision(entity);

        assertThat(entity.getActor()).isEqualTo("auditor@example.com");
        assertThat(entity.getCorrelationId()).isEqualTo("corr-456");
    }

    @Test
    void newRevision_WithoutAuthenticatedUser_SetsSystemActor() {
        AuditRevisionEntity entity = new AuditRevisionEntity();

        listener.newRevision(entity);

        assertThat(entity.getActor()).isEqualTo("SYSTEM");
        assertThat(entity.getCorrelationId()).isNull();
    }
}
