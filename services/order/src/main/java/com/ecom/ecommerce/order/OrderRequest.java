package com.ecom.ecommerce.order;

import com.ecom.ecommerce.product.PurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest (
        Integer id,
        String reference,
        @Positive(message = "Order amount must be positive")
        BigDecimal amount,
        @NotNull(message = "Payment method must be provided")
        PaymentMethod paymentMethod,
        @NotNull(message = "Customer should be present")
        @NotBlank(message = "Customer should be present")
        @NotEmpty(message = "Customer should be present")
        String customerId,
        @NotEmpty(message = "One product at least must be purchase")
        List<PurchaseRequest> products

) {
}
