package com.seguradora.msorder.infrastructure.config;

import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.PaymentEvent;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.SubscriptionEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração adicional do Kafka para eventos específicos de pagamento e subscrição
 */
@Configuration
public class KafkaEventConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ===============================
    // PAYMENT EVENT CONFIGURATION
    // ===============================

    @Bean
    public ProducerFactory<String, PaymentEvent> paymentEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate() {
        return new KafkaTemplate<>(paymentEventProducerFactory());
    }

    // ===============================
    // SUBSCRIPTION EVENT CONFIGURATION
    // ===============================

    @Bean
    public ProducerFactory<String, SubscriptionEvent> subscriptionEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, SubscriptionEvent> subscriptionEventKafkaTemplate() {
        return new KafkaTemplate<>(subscriptionEventProducerFactory());
    }
}
