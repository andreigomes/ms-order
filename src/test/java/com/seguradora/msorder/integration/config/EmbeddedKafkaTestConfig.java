package com.seguradora.msorder.integration.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

/**
 * Configuração específica para testes que precisam de Kafka embarcado funcionando
 */
@TestConfiguration
public class EmbeddedKafkaTestConfig {

    @Value("${spring.embedded.kafka.brokers}")
    private String kafkaBrokers;

    @Bean
    @Primary
    public ProducerFactory<String, Object> testProducerFactory() {
        Map<String, Object> producerProps = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.ACKS_CONFIG, "all",
            ProducerConfig.RETRIES_CONFIG, 3,
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true,
            ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1,
            ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000,
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000
        );
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> testKafkaTemplate(ProducerFactory<String, Object> testProducerFactory) {
        return new KafkaTemplate<>(testProducerFactory);
    }

    @Bean
    @Primary
    public ConsumerFactory<String, String> testConsumerFactory() {
        Map<String, Object> consumerProps = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers,
            ConsumerConfig.GROUP_ID_CONFIG, "test-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000,
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000
        );
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, String> testKafkaListenerContainerFactory(
            ConsumerFactory<String, String> testConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(testConsumerFactory);
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public NewTopic orderEventsTopic() {
        return new NewTopic("order-events", 1, (short) 1);
    }

    @Bean
    public NewTopic subscriptionEventsTopic() {
        return new NewTopic("subscription-events", 1, (short) 1);
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return new NewTopic("payment-events", 1, (short) 1);
    }
}
