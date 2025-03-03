package com.ecom.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderConfirmation> template;
    @Value("${kafka.topic.order}")
    private String orderTopic;

    public void sendOrderConfirmation(OrderConfirmation orderConfirmation) {

        log.info("Sending an order confirmation to topic {}", orderTopic);
        Message<OrderConfirmation> message = MessageBuilder
                .withPayload(orderConfirmation)
                .setHeader(KafkaHeaders.TOPIC, orderTopic)
                .build();
        template.send(message);
        log.info("Order confirmation ✔✔✔✔ sent to topic: {}", orderTopic);
    }
}
