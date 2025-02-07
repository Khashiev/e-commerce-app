package com.nazirka.ecommerce.kafka;

import com.nazirka.ecommerce.customer.CustomerResponse;
import com.nazirka.ecommerce.order.PaymentMethod;
import com.nazirka.ecommerce.product.PurchaseResponse;

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
