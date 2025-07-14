package com.seguradora.msorder.integration;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Teste de integração completo com Kafka embarcado
 * Verifica se a comunicação com Kafka está funcionando antes dos testes
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(
    partitions = 1,
    topics = {"order-events"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:0",
        "auto.create.topics.enable=true",
        "num.network.threads=3",
        "num.io.threads=8",
        "socket.send.buffer.bytes=102400",
        "socket.receive.buffer.bytes=102400",
        "socket.request.max.bytes=104857600"
    }
)
@DirtiesContext
@Transactional
class OrderIntegrationWithRealKafkaTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private FraudAnalysisPort fraudAnalysisPort;

    private KafkaConsumer<String, OrderEvent> kafkaConsumer;

    @BeforeEach
    void setUp() {
        // Aguardar broker estar completamente inicializado
        waitForBrokerInitialization();

        // Configurar consumer de forma mais robusta
        setupKafkaConsumer();

        // Verificar se Kafka está funcionando
        verifyKafkaConnection();
    }

    @AfterEach
    void tearDown() {
        if (kafkaConsumer != null) {
            kafkaConsumer.close();
        }
    }

    private void waitForBrokerInitialization() {
        Awaitility.await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(100))
            .until(() -> embeddedKafkaBroker.getBrokersAsString() != null);

        // Aguardar um pouco mais para garantir que os tópicos estejam criados
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void setupKafkaConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group-" + System.currentTimeMillis(), "true", embeddedKafkaBroker);

        // Configurações específicas para testes
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEvent.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        consumerProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
        consumerProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500");

        kafkaConsumer = new KafkaConsumer<>(consumerProps);
        kafkaConsumer.subscribe(Collections.singletonList("order-events"));

        // Aguardar atribuição de partições de forma mais robusta
        waitForPartitionAssignment();
    }

    private void waitForPartitionAssignment() {
        System.out.println("🔄 Aguardando atribuição de partições...");

        Awaitility.await()
            .atMost(Duration.ofSeconds(30))
            .pollInterval(Duration.ofMillis(200))
            .until(() -> {
                kafkaConsumer.poll(Duration.ofMillis(100));
                boolean hasPartitions = !kafkaConsumer.assignment().isEmpty();
                if (hasPartitions) {
                    System.out.println("✅ Partições atribuídas: " + kafkaConsumer.assignment());
                }
                return hasPartitions;
            });
    }

    void verifyKafkaConnection() {
        assertThat(embeddedKafkaBroker).isNotNull();
        assertThat(embeddedKafkaBroker.getBrokersAsString()).isNotBlank();
        System.out.println("✅ Kafka embarcado funcionando em: " + embeddedKafkaBroker.getBrokersAsString());
    }

    @Test
    void shouldCreateOrderAndPublishEventToRealKafka() {
        // Given - usando valores que passam na validação para cliente LOW risk
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST001",
            new BigDecimal("500.00"), // Valor menor que passa na validação para cliente LOW
            InsuranceType.AUTO,
            "Seguro auto para teste de integração"
        );

        // Mock da API de fraudes
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("LOW");

        // When - Criar pedido
        OrderResponse response = webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .returnResult()
                .getResponseBody();

        // Then - Verificar resposta
        assertThat(response).isNotNull();
        assertThat(response.customerId()).isEqualTo("CUST001");
        assertThat(response.status()).isEqualTo("VALIDATED"); // Estado após análise de fraudes

        // Then - Verificar se evento foi publicado no Kafka REAL
        ConsumerRecord<String, OrderEvent> record = KafkaTestUtils.getSingleRecord(
            kafkaConsumer,
            "order-events",
            Duration.ofSeconds(10)
        );

        assertThat(record).isNotNull();
        assertThat(record.value()).isNotNull();

        OrderEvent publishedEvent = record.value();
        assertThat(publishedEvent.orderId()).isEqualTo(response.id());
        assertThat(publishedEvent.eventType()).isEqualTo("ORDER_VALIDATED");

        System.out.println("✅ Evento publicado no Kafka: " + publishedEvent);
    }

    @Test
    void shouldPublishMultipleEventsToKafka() {
        // Given - usando valores menores que passam na validação
        CreateOrderRequest request1 = new CreateOrderRequest(
            "CUST002",
            new BigDecimal("400.00"), // Valor que passa para cliente REGULAR
            InsuranceType.HOME,
            "Seguro residencial"
        );

        CreateOrderRequest request2 = new CreateOrderRequest(
            "CUST003",
            new BigDecimal("300.00"), // Valor que passa para cliente REGULAR
            InsuranceType.LIFE,
            "Seguro de vida"
        );

        // Mock da API de fraudes
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        // When - Criar múltiplos pedidos
        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request1)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request2)
                .exchange()
                .expectStatus().isCreated();

        // Then - Verificar se múltiplos eventos foram publicados usando poll manual
        var records = kafkaConsumer.poll(Duration.ofSeconds(5));
        assertThat(records.count()).isEqualTo(2);

        System.out.println("✅ Múltiplos eventos publicados no Kafka: " + records.count());
    }
}
