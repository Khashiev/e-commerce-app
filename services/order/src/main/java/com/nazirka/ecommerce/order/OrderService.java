package com.nazirka.ecommerce.order;

import com.nazirka.ecommerce.customer.CustomerClient;
import com.nazirka.ecommerce.exception.BusinessException;
import com.nazirka.ecommerce.orderline.OrderLineRequest;
import com.nazirka.ecommerce.orderline.OrderLineService;
import com.nazirka.ecommerce.product.ProductClient;
import com.nazirka.ecommerce.product.PurchaseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(@Valid OrderRequest request) {
        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create customer with id " +
                        request.customerId()));

        this.productClient.purchaseProducts(request.products());

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

        return null;
    }
}
