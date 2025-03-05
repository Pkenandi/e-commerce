package com.ecom.ecommerce.kafka;

import com.ecom.ecommerce.email.EmailService;
import com.ecom.ecommerce.kafka.order.OrderConfirmation;
import com.ecom.ecommerce.kafka.payment.PaymentConfirmation;
import com.ecom.ecommerce.notification.Notification;
import com.ecom.ecommerce.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ecom.ecommerce.notification.NotificationType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) {
        logMessage("payment-topic", paymentConfirmation);
        repository.save(
                Notification.builder()
                        .notificationType(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        // send email
        sendEmail(paymentConfirmation.CustomerFirstname(), paymentConfirmation.customerLastname(),
                paymentConfirmation.customerEmail(), paymentConfirmation.amount(), paymentConfirmation.orderReference());

    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmation orderConfirmation) {
        logMessage("order-topic", orderConfirmation);
        repository.save(
                Notification.builder()
                        .notificationType(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        // send email
        sendEmail(orderConfirmation.customer().firstname(), orderConfirmation.customer().lastname(),
                orderConfirmation.customer().email(), orderConfirmation.totalAmount(), orderConfirmation.orderReference());
    }

    private void sendEmail(String customerFirstname, String customerLastname, String customerEmail,
                           BigDecimal amount, String orderReference) {
        emailService.sendPaymentSuccessEmail(
                customerEmail,
                customerFirstname + " " + customerLastname,
                amount,
                orderReference
        );
    }

    private void logMessage(String topic, Object object) {
        log.info("Consuming message from {} Topic:: {}", topic, object);
    }
}
