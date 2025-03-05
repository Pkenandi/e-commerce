package com.ecom.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaPaymentTopicConfig {

    @Bean
    public NewTopic PaymentTopic() {
        return TopicBuilder
                .name("payment-topic")
                .build();
    }
}
