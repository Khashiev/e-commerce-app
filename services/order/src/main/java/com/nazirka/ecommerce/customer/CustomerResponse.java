package com.nazirka.ecommerce.customer;

public record CustomerResponse(
        String id,
        String firstname,
        String lastName,
        String email
) {
}
