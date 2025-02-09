package com.nazirka.ecommerce.order;

import com.nazirka.ecommerce.customer.CustomerClient;
import com.nazirka.ecommerce.exception.BusinessException;
import com.nazirka.ecommerce.kafka.OrderConfirmation;
import com.nazirka.ecommerce.kafka.OrderProducer;
import com.nazirka.ecommerce.orderline.OrderLineRequest;
import com.nazirka.ecommerce.orderline.OrderLineService;
import com.nazirka.ecommerce.payment.PaymentClient;
import com.nazirka.ecommerce.payment.PaymentRequest;
import com.nazirka.ecommerce.product.ProductClient;
import com.nazirka.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(@Valid OrderRequest request) {
        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create customer with id " +
                        request.customerId()));

        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        var order = repository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException("Order with id " + orderId + " not found"));
    }
}
