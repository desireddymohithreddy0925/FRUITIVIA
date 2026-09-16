package com.fruitivia.payment.provider;

public interface PaymentProvider {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentStatus(String transactionReference);
}
