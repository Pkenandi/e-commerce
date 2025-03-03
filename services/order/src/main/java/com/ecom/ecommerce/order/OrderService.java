package com.ecom.ecommerce.order;

import com.ecom.ecommerce.customer.CustomerClient;
import com.ecom.ecommerce.exceptions.BusinessException;
import com.ecom.ecommerce.kafka.OrderConfirmation;
import com.ecom.ecommerce.kafka.OrderProducer;
import com.ecom.ecommerce.orderline.OrderLineRequest;
import com.ecom.ecommerce.orderline.OrderLineService;
import com.ecom.ecommerce.product.ProductClient;
import com.ecom.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final OrderMapper mapper;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createOrder(@Valid OrderRequest request) {
        // Check the customer with (Feign)
        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException(" Customer with ID:: %s was not found"
                        + request.customerId()));
        // purchase the products --> product-ms (RestTemplate)
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        // persist order
        var order = this.repository.save(mapper.toOrder(request));
        // persist order lines
        for (PurchaseRequest purchaseRequest: request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        // todo start payment process

        // send the order confirmation email --> notification-ms (kafka)
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
                .orElseThrow(
                        () -> new EntityNotFoundException(format(" An Order was not found with the provided Order ID %s"
                        , orderId))
                );
    }
}
