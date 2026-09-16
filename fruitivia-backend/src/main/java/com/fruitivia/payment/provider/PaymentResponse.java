package com.fruitivia.payment.provider;

import com.fruitivia.payment.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String transactionReference;
    private PaymentStatus status;
    private String message;
}
