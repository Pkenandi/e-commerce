package com.ecom.ecommerce.exceptions.handler;

import java.util.HashMap;

public record ErrorResponse(
        HashMap<String, String> errors
) {
}
