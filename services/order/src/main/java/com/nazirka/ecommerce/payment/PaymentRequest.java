package com.nazirka.ecommerce.payment;

import com.nazirka.ecommerce.customer.CustomerResponse;
import com.nazirka.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
