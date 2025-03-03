package com.ecom.ecommerce.exception.handler;

import java.util.HashMap;

public record ErrorResponse(
        HashMap<String, String> errors
) {
}
