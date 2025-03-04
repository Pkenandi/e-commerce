package com.ecom.ecommerce.kafka.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record Customer(
        String id,
        String firstname,
        String lastname,
        String email
) {
}
