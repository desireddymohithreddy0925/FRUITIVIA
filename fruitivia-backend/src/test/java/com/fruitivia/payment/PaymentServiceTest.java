package com.fruitivia.payment;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.buyer.BuyerRepository;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.payment.provider.PaymentProvider;
import com.fruitivia.payment.provider.PaymentRequest;
import com.fruitivia.payment.provider.PaymentResponse;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fruitivia.common.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentStatusHistoryRepository historyRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private PaymentProvider paymentProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BuyerRepository buyerRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private User buyerUser;
    private Buyer buyer;
    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        buyerUser = User.builder()
                .email("buyer@test.com")
                .role(Role.BUYER)
                .build();
        buyerUser.setId(UUID.randomUUID());

        buyer = Buyer.builder()
                .companyName("Test Buyer")
                .user(buyerUser)
                .build();
        buyer.setId(UUID.randomUUID());

        order = Order.builder()
                .orderNumber("ORD-2026-0001")
                .totalAmount(new BigDecimal("1000.00"))
                .currencyCode("USD")
                .buyer(buyer)
                .build();
        order.setId(UUID.randomUUID());

        request = PaymentRequest.builder()
                .amount(new BigDecimal("1000.00"))
                .currencyCode("USD")
                .method(PaymentMethod.CREDIT_CARD)
                .build();
    }

    private void setupSecurityContext() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(buyerUser.getEmail(), null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByEmail(buyerUser.getEmail())).thenReturn(Optional.of(buyerUser));
        when(buyerRepository.findByUserId(buyerUser.getId())).thenReturn(Optional.of(buyer));
    }

    @Test
    void testProcessPayment_Success() {
        setupSecurityContext();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)).thenReturn(false);
        
        Payment mockSavedPayment = new Payment();
        mockSavedPayment.setId(UUID.randomUUID());
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockSavedPayment);

        PaymentResponse mockResponse = PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .transactionReference("TXN123")
                .message("Success")
                .build();
        when(paymentProvider.processPayment(any(PaymentRequest.class))).thenReturn(mockResponse);

        Payment result = paymentService.processPayment(order.getId(), request);

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(eventPublisher).publish(any(com.fruitivia.payment.event.PaymentSuccessEvent.class));
    }

    @Test
    void testProcessPayment_DuplicatePaymentRejected() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> paymentService.processPayment(order.getId(), request));
    }

    @Test
    void testProcessPayment_AmountMismatchRejected() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)).thenReturn(false);

        request.setAmount(new BigDecimal("500.00")); // Mismatch

        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(order.getId(), request));
    }

    @Test
    void testProcessPayment_CurrencyMismatchRejected() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)).thenReturn(false);

        request.setCurrencyCode("EUR"); // Mismatch

        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(order.getId(), request));
    }

    @Test
    void testProcessPayment_UnauthorizedBuyerRejected() {
        User otherBuyerUser = User.builder().email("other@test.com").role(Role.BUYER).build();
        otherBuyerUser.setId(UUID.randomUUID());
        Buyer otherBuyer = Buyer.builder().user(otherBuyerUser).build();
        otherBuyer.setId(UUID.randomUUID());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(otherBuyerUser.getEmail(), null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)).thenReturn(false);
        when(userRepository.findByEmail(otherBuyerUser.getEmail())).thenReturn(Optional.of(otherBuyerUser));
        when(buyerRepository.findByUserId(otherBuyerUser.getId())).thenReturn(Optional.of(otherBuyer));

        assertThrows(AccessDeniedException.class, () -> paymentService.processPayment(order.getId(), request));
    }
}
