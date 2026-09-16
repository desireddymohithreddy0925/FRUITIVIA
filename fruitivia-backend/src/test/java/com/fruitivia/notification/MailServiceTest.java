package com.fruitivia.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private MailService mailService;

    @Test
    void sendEmailIdempotent_SendsEmail_WhenKeyDoesNotExist() {
        String key = "test_key_1";
        when(notificationLogRepository.existsByIdempotencyKey(key)).thenReturn(false);

        mailService.sendEmailIdempotent(key, "buyer@test.com", "Test Subject", "Test Text", NotificationType.ORDER);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly("buyer@test.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Test Subject");
        assertThat(sentMessage.getText()).isEqualTo("Test Text");

        verify(notificationLogRepository).save(any(NotificationLog.class));
    }

    @Test
    void sendEmailIdempotent_SkipsSending_WhenKeyExists() {
        String key = "test_key_1";
        when(notificationLogRepository.existsByIdempotencyKey(key)).thenReturn(true);

        mailService.sendEmailIdempotent(key, "buyer@test.com", "Test Subject", "Test Text", NotificationType.ORDER);

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
        verify(notificationLogRepository, never()).save(any(NotificationLog.class));
    }
}
