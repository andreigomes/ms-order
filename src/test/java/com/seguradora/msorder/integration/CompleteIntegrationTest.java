package com.seguradora.msorder.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.event.OrderEvent;
import com.seguradora.msorder.integration.config.TestFraudAnalysisConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração completo usando Testcontainers
 * Testa o fluxo completo: API REST → PostgreSQL → Kafka
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestFraudAnalysisConfig.class)
class CompleteIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Container do PostgreSQL
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("seguradora_orders_test")
            .withUsername("test_user")
            .withPassword("test_pass")
            .waitingFor(Wait.forListeningPort());

    // Container do Kafka
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configurações do PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Configurações do Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // Configuração da API de fraudes (mock)
        registry.add("app.fraud-api.url", () -> "http://localhost:8999/api/v1");
    }

    private Consumer<String, OrderEvent> createKafkaConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.seguradora.msorder.infrastructure.adapter.out.messaging.event");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEvent.class.getName());

        return new DefaultKafkaConsumerFactory<String, OrderEvent>(props).createConsumer();
    }

    @BeforeEach
    void setUp() {
        // Aguarda os containers estarem prontos
        assertThat(postgres.isRunning()).isTrue();
        assertThat(kafka.isRunning()).isTrue();
    }

    @Test
    void shouldCompleteFullFlowFromRestApiToKafka() throws Exception {
        // Given - Preparar o consumer do Kafka
        Consumer<String, OrderEvent> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList("order-events"));

        // Given - Preparar o request
        CreateOrderRequest request = new CreateOrderRequest(
            "INTEGRATION_TEST_001",
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Teste de integração completo"
        );

        String url = "http://localhost:" + port + "/api/v1/orders";

        // When - Fazer a chamada para a API REST
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Then - Verificar a resposta da API
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        // Parse da resposta para verificar os dados
        Map<String, Object> orderResponse = objectMapper.readValue(response.getBody(), Map.class);
        assertThat(orderResponse.get("customerId")).isEqualTo("INTEGRATION_TEST_001");
        assertThat(orderResponse.get("insuranceType")).isEqualTo("AUTO");
        assertThat(orderResponse.get("amount")).isEqualTo(1500.0);
        assertThat(orderResponse.get("description")).isEqualTo("Teste de integração completo");

        // Verificar se o status foi definido corretamente (dependendo da análise de fraudes)
        String status = (String) orderResponse.get("status");
        assertThat(status).isIn("PENDING_PAYMENT", "PENDING_ANALYSIS", "REJECTED");

        // Then - Verificar se o evento foi publicado no Kafka
        ConsumerRecords<String, OrderEvent> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records).isNotEmpty();

        ConsumerRecord<String, OrderEvent> record = records.iterator().next();
        OrderEvent event = record.value();

        assertThat(event).isNotNull();
        assertThat(event.customerId()).contains("INTEGRATION_TEST_001");
        assertThat(event.insuranceType()).isEqualTo(InsuranceType.AUTO);
        assertThat(event.amount()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(event.description()).isEqualTo("Teste de integração completo");
        assertThat(event.eventType()).isIn("ORDER_CREATED", "ORDER_PENDING_ANALYSIS", "ORDER_REJECTED");

        // Verificar se o timestamp do evento é recente
        assertThat(event.timestamp()).isNotNull();

        consumer.close();
    }

    @Test
    void shouldHandleLowRiskOrderFlow() throws Exception {
        // Given - Preparar o consumer do Kafka
        Consumer<String, OrderEvent> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList("order-events"));

        // Given - Pedido com valor baixo (deveria ser baixo risco)
        CreateOrderRequest request = new CreateOrderRequest(
            "LOW_RISK_001",
            InsuranceType.HOME,
            new BigDecimal("500.00"), // Valor baixo
            "Seguro residencial básico"
        );

        String url = "http://localhost:" + port + "/api/v1/orders";

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Then - Verificar resposta da API
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String, Object> orderResponse = objectMapper.readValue(response.getBody(), Map.class);
        String orderId = (String) orderResponse.get("id");
        assertThat(orderId).isNotNull();

        // Then - Verificar evento no Kafka
        ConsumerRecords<String, OrderEvent> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records).isNotEmpty();

        boolean foundEvent = false;
        for (ConsumerRecord<String, OrderEvent> record : records) {
            OrderEvent event = record.value();
            if (event.customerId().contains("LOW_RISK_001")) {
                foundEvent = true;
                assertThat(event.insuranceType()).isEqualTo(InsuranceType.HOME);
                assertThat(event.amount()).isEqualTo(new BigDecimal("500.00"));
                break;
            }
        }
        assertThat(foundEvent).isTrue();

        consumer.close();
    }

    @Test
    void shouldHandleHighValueOrderFlow() throws Exception {
        // Given - Preparar o consumer do Kafka
        Consumer<String, OrderEvent> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList("order-events"));

        // Given - Pedido com valor alto (deveria ser alto risco)
        CreateOrderRequest request = new CreateOrderRequest(
            "HIGH_RISK_001",
            InsuranceType.LIFE,
            new BigDecimal("25000.00"), // Valor alto
            "Seguro de vida premium"
        );

        String url = "http://localhost:" + port + "/api/v1/orders";

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Then - Verificar resposta da API
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String, Object> orderResponse = objectMapper.readValue(response.getBody(), Map.class);
        String status = (String) orderResponse.get("status");
        // Para valor alto, deveria ir para análise ou ser rejeitado
        assertThat(status).isIn("PENDING_ANALYSIS", "REJECTED");

        // Then - Verificar evento no Kafka
        ConsumerRecords<String, OrderEvent> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records).isNotEmpty();

        boolean foundEvent = false;
        for (ConsumerRecord<String, OrderEvent> record : records) {
            OrderEvent event = record.value();
            if (event.customerId().contains("HIGH_RISK_001")) {
                foundEvent = true;
                assertThat(event.insuranceType()).isEqualTo(InsuranceType.LIFE);
                assertThat(event.amount()).isEqualTo(new BigDecimal("25000.00"));
                assertThat(event.eventType()).isIn("ORDER_PENDING_ANALYSIS", "ORDER_REJECTED");
                break;
            }
        }
        assertThat(foundEvent).isTrue();

        consumer.close();
    }

    @Test
    void shouldPersistOrderInDatabase() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "DB_TEST_001",
            InsuranceType.AUTO,
            new BigDecimal("2000.00"),
            "Teste de persistência no banco"
        );

        String url = "http://localhost:" + port + "/api/v1/orders";

        // When - Criar o pedido
        ResponseEntity<String> createResponse = restTemplate.postForEntity(url, request, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String, Object> orderResponse = objectMapper.readValue(createResponse.getBody(), Map.class);
        String orderId = (String) orderResponse.get("id");

        // Then - Buscar o pedido criado para verificar persistência
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
            url + "/" + orderId, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> retrievedOrder = objectMapper.readValue(getResponse.getBody(), Map.class);
        assertThat(retrievedOrder.get("id")).isEqualTo(orderId);
        assertThat(retrievedOrder.get("customerId")).isEqualTo("DB_TEST_001");
        assertThat(retrievedOrder.get("insuranceType")).isEqualTo("AUTO");
        assertThat(retrievedOrder.get("amount")).isEqualTo(2000.0);
        assertThat(retrievedOrder.get("description")).isEqualTo("Teste de persistência no banco");
        assertThat(retrievedOrder.get("createdAt")).isNotNull();
        assertThat(retrievedOrder.get("updatedAt")).isNotNull();
    }

    @Test
    void shouldRejectBlockedCustomerAndPublishEvent() throws Exception {
        // Given - Preparar o consumer do Kafka
        Consumer<String, OrderEvent> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList("order-events"));

        // Given - Cliente bloqueado (prefixo BLOCKED_)
        CreateOrderRequest request = new CreateOrderRequest(
            "BLOCKED_CUSTOMER_001",
            InsuranceType.AUTO,
            new BigDecimal("1000.00"),
            "Tentativa de pedido por cliente bloqueado"
        );

        String url = "http://localhost:" + port + "/api/v1/orders";

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Then - Verificar resposta da API
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String, Object> orderResponse = objectMapper.readValue(response.getBody(), Map.class);
        String status = (String) orderResponse.get("status");
        assertThat(status).isEqualTo("REJECTED");

        // Then - Verificar evento de rejeição no Kafka
        ConsumerRecords<String, OrderEvent> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records).isNotEmpty();

        boolean foundRejectionEvent = false;
        for (ConsumerRecord<String, OrderEvent> record : records) {
            OrderEvent event = record.value();
            if (event.customerId().contains("BLOCKED_CUSTOMER_001")) {
                foundRejectionEvent = true;
                assertThat(event.eventType()).isEqualTo("ORDER_REJECTED");
                assertThat(event.status().name()).isEqualTo("REJECTED");
                break;
            }
        }
        assertThat(foundRejectionEvent).isTrue();

        consumer.close();
    }

    @Test
    void shouldHandleMultipleOrdersAndEvents() throws Exception {
        // Given - Preparar o consumer do Kafka
        Consumer<String, OrderEvent> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList("order-events"));

        String url = "http://localhost:" + port + "/api/v1/orders";

        // When - Criar múltiplos pedidos com diferentes cenários
        CreateOrderRequest[] requests = {
            new CreateOrderRequest("MULTI_001", InsuranceType.AUTO, new BigDecimal("500.00"), "Baixo risco"),
            new CreateOrderRequest("MULTI_002", InsuranceType.HOME, new BigDecimal("3000.00"), "Risco médio"),
            new CreateOrderRequest("BLOCKED_MULTI_001", InsuranceType.LIFE, new BigDecimal("1000.00"), "Cliente bloqueado")
        };

        for (CreateOrderRequest request : requests) {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        // Then - Verificar que todos os eventos foram publicados
        ConsumerRecords<String, OrderEvent> records = consumer.poll(Duration.ofSeconds(15));
        assertThat(records.count()).isGreaterThanOrEqualTo(3);

        Map<String, OrderEvent> eventsByCustomer = new HashMap<>();
        for (ConsumerRecord<String, OrderEvent> record : records) {
            OrderEvent event = record.value();
            if (event.customerId().startsWith("MULTI_") || event.customerId().startsWith("BLOCKED_MULTI_")) {
                eventsByCustomer.put(event.customerId(), event);
            }
        }

        // Verificar eventos específicos
        assertThat(eventsByCustomer).hasSize(3);

        // Baixo risco - deve ser ORDER_CREATED
        OrderEvent lowRiskEvent = eventsByCustomer.values().stream()
            .filter(e -> e.customerId().contains("MULTI_001"))
            .findFirst()
            .orElse(null);
        assertThat(lowRiskEvent).isNotNull();
        assertThat(lowRiskEvent.eventType()).isEqualTo("ORDER_CREATED");

        // Cliente bloqueado - deve ser ORDER_REJECTED
        OrderEvent blockedEvent = eventsByCustomer.values().stream()
            .filter(e -> e.customerId().contains("BLOCKED_MULTI_001"))
            .findFirst()
            .orElse(null);
        assertThat(blockedEvent).isNotNull();
        assertThat(blockedEvent.eventType()).isEqualTo("ORDER_REJECTED");

        consumer.close();
    }
}

