package com.ecom.ecommerce.payment;

import com.ecom.ecommerce.customer.CustomerResponse;
import com.ecom.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
