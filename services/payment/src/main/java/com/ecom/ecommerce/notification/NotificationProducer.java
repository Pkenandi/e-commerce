package com.ecom.ecommerce.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, PaymentNotificationRequest> template;

    public void sendNotification(PaymentNotificationRequest request) {
        log.info(" Sending notification with body <{}> ", request);
        Message<PaymentNotificationRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(TOPIC, "payment-topic")
                .build();
        template.send(message);
    }
}