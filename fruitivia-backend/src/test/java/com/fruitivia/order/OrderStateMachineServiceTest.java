package com.fruitivia.order;

import com.fruitivia.inventory.InventoryService;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStateMachineServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderStatusHistoryRepository historyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private OrderItemAllocationRepository allocationRepository;

    @InjectMocks
    private OrderStateMachineService stateMachineService;

    private Order order;
    private User adminUser;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .status(OrderStatus.QUOTE_ACCEPTED)
                .build();
        order.setId(UUID.randomUUID());

        adminUser = User.builder()
                .email("admin@fruitivia.com")
                .role(Role.ADMIN)
                .build();
        adminUser.setId(UUID.randomUUID());
    }

    private void setupSecurityContext(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testValidTransition_QuoteAcceptedToPaymentPending() {
        setupSecurityContext(adminUser);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Order result = stateMachineService.transitionState(order.getId(), OrderStatus.PAYMENT_PENDING, "Waiting for payment");

        assertEquals(OrderStatus.PAYMENT_PENDING, result.getStatus());
        verify(orderRepository).save(order);
        verify(historyRepository).save(any(OrderStatusHistory.class));
    }

    @Test
    void testInvalidTransition_QuoteAcceptedToPacked() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> {
            stateMachineService.transitionState(order.getId(), OrderStatus.PACKED, "Try packing");
        });
        
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testUnauthorizedTransition_BuyerTryingToMarkPaid() {
        User buyerUser = User.builder().email("buyer@test.com").role(Role.BUYER).build();
        setupSecurityContext(buyerUser);
        
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> {
            stateMachineService.transitionState(order.getId(), OrderStatus.PAID, "I paid");
        });
    }

    @Test
    void testValidTransition_PaidToProcessing() {
        setupSecurityContext(adminUser);
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Order result = stateMachineService.transitionState(order.getId(), OrderStatus.PROCESSING, "Start processing");

        assertEquals(OrderStatus.PROCESSING, result.getStatus());
        // verify inventory reservation called
        // Since there are no items, it won't loop, but it shouldn't throw error
    }
}
