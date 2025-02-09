package com.nazirka.ecommerce.payment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record Customer(
        String id,
        @NotNull(message = "First name is required")
        String firstName,
        @NotNull(message = "last name is required")
        String lastName,
        @NotNull(message = "email name is required")
        @Email(message = "The customer is not correctly formated")
        String email
) {
}
