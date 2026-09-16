package com.fruitivia.payment;

import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.payment.event.PaymentFailedEvent;
import com.fruitivia.payment.event.PaymentSuccessEvent;
import com.fruitivia.payment.provider.PaymentProvider;
import com.fruitivia.payment.provider.PaymentRequest;
import com.fruitivia.payment.provider.PaymentResponse;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;
    private final OrderRepository orderRepository;
    private final PaymentProvider paymentProvider;
    private final com.fruitivia.common.event.EventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final com.fruitivia.buyer.BuyerRepository buyerRepository;

    @Transactional
    public Payment processPayment(UUID orderId, PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Validation 1: Prevent duplicate payment
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("Order already has a successful payment");
        }

        // Validation 2: Verify amount
        if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("Payment amount does not match order total");
        }

        // Validation 3: Verify currency
        if (!request.getCurrencyCode().equalsIgnoreCase(order.getCurrencyCode())) {
            throw new IllegalArgumentException("Payment currency does not match order currency");
        }

        // Validation 4: Buyer ownership
        User currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getRole() == com.fruitivia.user.Role.BUYER) {
            com.fruitivia.buyer.Buyer buyer = buyerRepository.findByUserId(currentUser.getId()).orElse(null);
            if (buyer == null || !buyer.getId().equals(order.getBuyer().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Cannot process payment for an order you do not own");
            }
        }

        // Initial Payment record creation (PENDING)
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .currencyCode(request.getCurrencyCode())
                .method(request.getMethod())
                .status(PaymentStatus.PROCESSING)
                .build();
        payment.setActive(true);
        payment = paymentRepository.save(payment);

        recordHistory(payment, PaymentStatus.PROCESSING, "Initiated payment processing");

        // Communicate with Provider
        request.setOrderNumber(order.getOrderNumber());
        PaymentResponse response = paymentProvider.processPayment(request);

        // Process response
        payment.setTransactionReference(response.getTransactionReference());
        
        if (response.getStatus() == PaymentStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(Instant.now());
            payment = paymentRepository.save(payment);
            recordHistory(payment, PaymentStatus.SUCCESS, response.getMessage());

            eventPublisher.publish(new PaymentSuccessEvent(this, order.getId(), payment.getId()));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);
            recordHistory(payment, PaymentStatus.FAILED, response.getMessage());

            eventPublisher.publish(new PaymentFailedEvent(this, order.getId(), payment.getId(), response.getMessage()));
        }

        return payment;
    }

    private void recordHistory(Payment payment, PaymentStatus status, String notes) {
        PaymentStatusHistory history = PaymentStatusHistory.builder()
                .payment(payment)
                .status(status)
                .actor(getCurrentUser())
                .notes(notes)
                .build();
        history.setActive(true);
        historyRepository.save(history);
    }

    private User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
