package com.ecom.ecommerce.kafka;

import com.ecom.ecommerce.customer.CustomerResponse;
import com.ecom.ecommerce.order.PaymentMethod;
import com.ecom.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
