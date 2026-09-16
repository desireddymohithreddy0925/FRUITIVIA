package com.fruitivia.payment.provider;

import com.fruitivia.payment.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        String transactionReference = "MOCK-TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Simulate failure for specific amounts or tokens
        if (request.getPaymentToken() != null && request.getPaymentToken().equals("tok_fail")) {
            return PaymentResponse.builder()
                    .transactionReference(transactionReference)
                    .status(PaymentStatus.FAILED)
                    .message("Insufficient funds")
                    .build();
        }

        // Default simulate success
        return PaymentResponse.builder()
                .transactionReference(transactionReference)
                .status(PaymentStatus.SUCCESS)
                .message("Payment processed successfully")
                .build();
    }

    @Override
    public PaymentResponse getPaymentStatus(String transactionReference) {
        // Normally this would query an external gateway. Here we mock it.
        return PaymentResponse.builder()
                .transactionReference(transactionReference)
                .status(PaymentStatus.SUCCESS)
                .message("Payment is successful")
                .build();
    }
}
