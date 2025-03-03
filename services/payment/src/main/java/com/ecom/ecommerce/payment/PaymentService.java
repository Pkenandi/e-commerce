package com.ecom.ecommerce.payment;

import com.ecom.ecommerce.notification.NotificationProducer;
import com.ecom.ecommerce.notification.PaymentNotificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationProducer notificationProducer;
    private final PaymentMapper mapper;

    public Integer processPayment(@Valid PaymentRequest paymentRequest) {
        var payment = paymentRepository.save(mapper.toPayment(paymentRequest))
                .getId();

        notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        paymentRequest.orderReference(),
                        paymentRequest.amount(),
                        paymentRequest.paymentMethod(),
                        paymentRequest.customer().firstname(),
                        paymentRequest.customer().lastname(),
                        paymentRequest.customer().email()
                )
        );
        return payment;
    }
}
