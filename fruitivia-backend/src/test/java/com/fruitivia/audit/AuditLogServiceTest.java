package com.fruitivia.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void logEvent_WithAuthenticatedUser_LogsCorrectActor() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser@example.com", "password", java.util.Collections.emptyList())
        );
        MDC.put("correlationId", "corr-123");

        auditLogService.logEvent("TEST_ACTION", "TEST_RESOURCE", "1", "Test metadata");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getActor()).isEqualTo("testuser@example.com");
        assertThat(savedLog.getAction()).isEqualTo("TEST_ACTION");
        assertThat(savedLog.getResourceType()).isEqualTo("TEST_RESOURCE");
        assertThat(savedLog.getResourceId()).isEqualTo("1");
        assertThat(savedLog.getMetadata()).isEqualTo("Test metadata");
        assertThat(savedLog.getCorrelationId()).isEqualTo("corr-123");
        assertThat(savedLog.getTimestamp()).isNotNull();
    }

    @Test
    void logEvent_WithoutAuthenticatedUser_LogsSystemActor() {
        auditLogService.logEvent("TEST_ACTION", "TEST_RESOURCE", "1", "Test metadata");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getActor()).isEqualTo("SYSTEM");
    }
}
