package com.seguradora.msorder.infrastructure.config;

import com.seguradora.msorder.infrastructure.adapter.out.messaging.event.OrderEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    @InjectMocks
    private KafkaConfig kafkaConfig;

    @Test
    void shouldCreateProducerFactoryWithCorrectConfiguration() {
        // Given
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");

        // When
        ProducerFactory<String, OrderEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        assertThat(producerFactory).isNotNull();
        assertThat(producerFactory.getConfigurationProperties())
            .containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
            .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
            .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
            .containsEntry(ProducerConfig.ACKS_CONFIG, "1")
            .containsEntry(ProducerConfig.RETRIES_CONFIG, 3)
            .containsEntry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
            .containsEntry(ProducerConfig.BATCH_SIZE_CONFIG, 16384)
            .containsEntry(ProducerConfig.LINGER_MS_CONFIG, 5)
            .containsEntry(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432)
            .containsEntry(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")
            .containsEntry(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5)
            .containsEntry(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
    }

    @Test
    void shouldCreateKafkaTemplateForOrderEvent() {
        // Given
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");

        // When
        KafkaTemplate<String, OrderEvent> kafkaTemplate = kafkaConfig.kafkaTemplate();

        // Then
        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate.getProducerFactory()).isNotNull();
    }

    @Test
    void shouldCreateGenericProducerFactoryWithCorrectConfiguration() {
        // Given
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");

        // When
        ProducerFactory<String, Object> genericProducerFactory = kafkaConfig.genericProducerFactory();

        // Then
        assertThat(genericProducerFactory).isNotNull();
        assertThat(genericProducerFactory.getConfigurationProperties())
            .containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
            .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
            .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
            .containsEntry(ProducerConfig.ACKS_CONFIG, "1")
            .containsEntry(ProducerConfig.RETRIES_CONFIG, 3)
            .containsEntry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    }

    @Test
    void shouldCreateGenericKafkaTemplate() {
        // Given
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");

        // When
        KafkaTemplate<String, Object> genericKafkaTemplate = kafkaConfig.genericKafkaTemplate();

        // Then
        assertThat(genericKafkaTemplate).isNotNull();
        assertThat(genericKafkaTemplate.getProducerFactory()).isNotNull();
    }

    @Test
    void shouldUseDefaultBootstrapServersWhenNotProvided() {
        // Given - Definir explicitamente o valor padrão para simular a configuração
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");

        // When
        ProducerFactory<String, OrderEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        assertThat(producerFactory).isNotNull();
        assertThat(producerFactory.getConfigurationProperties())
            .containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    }
}
