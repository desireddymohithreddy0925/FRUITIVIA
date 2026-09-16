package com.fruitivia.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByTransactionReference(String transactionReference);
    boolean existsByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
