package com.fruitivia.payment.provider;

import com.fruitivia.payment.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    private String orderNumber;
    private BigDecimal amount;
    private String currencyCode;
    private PaymentMethod method;
    // We do NOT include card details or sensitive credentials here. 
    // If a real provider is integrated, a secure token provided by frontend will be passed instead.
    private String paymentToken;
}
