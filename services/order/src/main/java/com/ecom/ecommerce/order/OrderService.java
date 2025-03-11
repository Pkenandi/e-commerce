package com.ecom.ecommerce.order;

import com.ecom.ecommerce.customer.CustomerClient;
import com.ecom.ecommerce.customer.CustomerResponse;
import com.ecom.ecommerce.exceptions.BusinessException;
import com.ecom.ecommerce.kafka.OrderConfirmation;
import com.ecom.ecommerce.kafka.OrderProducer;
import com.ecom.ecommerce.orderline.OrderLine;
import com.ecom.ecommerce.orderline.OrderLineRequest;
import com.ecom.ecommerce.orderline.OrderLineService;
import com.ecom.ecommerce.payment.PaymentClient;
import com.ecom.ecommerce.payment.PaymentRequest;
import com.ecom.ecommerce.product.ProductClient;
import com.ecom.ecommerce.product.PurchaseRequest;
import com.ecom.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final OrderMapper mapper;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(@Valid OrderRequest request) {
        // Check the customer with (Feign)
        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException(" Customer with ID:: %s was not found"
                        + request.customerId()));
        // purchase the products --> product-ms (RestTemplate)
        log.info("START PRODUCT PURCHASE");
        var purchasedProducts = this.productClient.purchaseProducts(request.products());
        log.info("END PRODUCT PURCHASE");
        // persist order & orderLine
        log.info("START PERSISTING ORDER");
        var order = persistOrder(request);
        log.info("END PERSISTING ORDER");
        // start payment process
        log.info("START PAYMENT");
        proceedToPayment(request, customer);
        log.info("END PAYMENT");
        // send the order confirmation email --> notification-ms (kafka)
        log.info("START ORDER CONFIRMATION");
        sendOrderConfirmation(request, customer, purchasedProducts);
        log.info("START ORDER CONFIRMATION");

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(
                        () -> new EntityNotFoundException(format(" An Order was not found with the provided Order ID %s"
                        , orderId))
                );
    }

    private void sendOrderConfirmation(OrderRequest request, CustomerResponse customer, List<PurchaseResponse> purchasedProducts) {
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
    }

    private void proceedToPayment(OrderRequest request, CustomerResponse customer) {
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                request.id(),
                request.reference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);
    }

    private Order persistOrder(OrderRequest request) {
        var order = this.repository.save(mapper.toOrder(request));

        log.info("ORDER CREATED ");

        // persist order lines
        var orderLineRequests = request.products()
                        .stream()
                                .map(purchaseRequest -> new OrderLineRequest(
                                        null,
                                        order.getId(),
                                        purchaseRequest.productId(),
                                        purchaseRequest.quantity()
                                ))
                                .toList();
        var orderLines = orderLineRequests
                .stream()
                        .toList();

        orderLineService.saveAllOrderLines(orderLines);
        log.info("ORDERLINES CREATED ");
//        request.products().forEach(purchaseRequest -> {
//                    orderLineService.saveOrderLine(
//                        new OrderLineRequest(
//                                null,
//                                order.getId(),
//                                purchaseRequest.productId(),
//                                purchaseRequest.quantity()
//                        )
//                    );
//                }
//        );
//        for (PurchaseRequest purchaseRequest: request.products()) {
//            orderLineService.saveOrderLine(
//                    new OrderLineRequest(
//                            null,
//                            order.getId(),
//                            purchaseRequest.productId(),
//                            purchaseRequest.quantity()
//                    )
//            );
//        }
        return order;
    }
}
