package com.ecom.ecommerce.payment;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {
    public Payment toPayment(@Valid PaymentRequest paymentRequest) {
        return Payment.builder()
                .id(paymentRequest.id())
                .orderId(paymentRequest.orderId())
                .paymentMethod(paymentRequest.paymentMethod())
                .amount(paymentRequest.amount())
                .build();
    }
}
