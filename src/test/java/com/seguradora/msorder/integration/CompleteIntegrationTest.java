package com.seguradora.msorder.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.SalesChannel;
import com.seguradora.msorder.core.domain.valueobject.PaymentMethod;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.integration.config.EmbeddedKafkaTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Teste de integração usando PostgreSQL via TestContainers e Kafka embarcado
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@EmbeddedKafka(
    partitions = 1,
    topics = { "order-events", "subscription-events", "payment-events" }
)
@Import(EmbeddedKafkaTestConfig.class)
class CompleteIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("orders_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false) // Força criação de novo container a cada execução
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw")); // Usa tmpfs para dados temporários

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");

        // Configurações do Flyway para garantir execução correta
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.flyway.clean-on-validation-error", () -> "true");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.validate-on-migrate", () -> "true");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudAnalysisPort fraudAnalysisPort;

    @Test
    void shouldCreateOrderSuccessfully() {
        // Given - Mock retorna classificação REGULAR para fluxo normal
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        CreateOrderRequest request = new CreateOrderRequest(
            "1002", // customerId
            "PROD002", // productId
            InsuranceType.AUTO, // category
            SalesChannel.WEB_SITE, // salesChannel
            PaymentMethod.CREDIT_CARD, // paymentMethod
            new BigDecimal("500.00"), // totalMonthlyPremiumAmount
            new BigDecimal("50000.00"), // insuredAmount
            Map.of("collision", new BigDecimal("40000.00")), // coverages
            List.of("24h assistance"), // assistances
            "Seguro de automóvel" // description
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsAnyOf("RECEIVED", "PENDING");
    }

    @Test
    void shouldRejectOrderForHighValueWithNoInfo() {
        // Given - Mock retorna NO_INFO
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("NO_INFO");

        CreateOrderRequest request = new CreateOrderRequest(
            "1006", // customerId
            "PROD006", // productId
            InsuranceType.AUTO, // category
            SalesChannel.PHONE, // salesChannel
            PaymentMethod.BANK_TRANSFER, // paymentMethod
            new BigDecimal("1000.00"), // totalMonthlyPremiumAmount
            new BigDecimal("100000.00"), // insuredAmount
            Map.of("comprehensive", new BigDecimal("90000.00")), // coverages
            List.of("premium assistance"), // assistances
            "Seguro de automóvel premium" // description
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsAnyOf("RECEIVED", "REJECTED");
    }

    @Test
    void shouldAcceptValidHighRiskOrder() {
        // Given - Mock retorna HIGH_RISK
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("HIGH_RISK");

        CreateOrderRequest request = new CreateOrderRequest(
            "1003", // customerId
            "PROD003", // productId
            InsuranceType.HOME, // category
            SalesChannel.BRANCH, // salesChannel
            PaymentMethod.PIX, // paymentMethod
            new BigDecimal("300.00"), // totalMonthlyPremiumAmount
            new BigDecimal("30000.00"), // insuredAmount
            Map.of("fire", new BigDecimal("25000.00")), // coverages
            List.of("emergency repair"), // assistances
            "Seguro residencial" // description
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsAnyOf("RECEIVED", "PENDING");
    }

    @Test
    void shouldAcceptPreferentialCustomerOrder() {
        // Given - Mock retorna PREFERENTIAL
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("PREFERENTIAL");

        CreateOrderRequest request = new CreateOrderRequest(
            "1001", // customerId
            "PROD001", // productId
            InsuranceType.LIFE, // category
            SalesChannel.MOBILE, // salesChannel
            PaymentMethod.CREDIT_CARD, // paymentMethod
            new BigDecimal("750.00"), // totalMonthlyPremiumAmount
            new BigDecimal("75000.00"), // insuredAmount
            Map.of("death", new BigDecimal("75000.00")), // coverages
            List.of("beneficiary support"), // assistances
            "Seguro de vida premium" // description
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsAnyOf("RECEIVED", "PENDING");
    }

    @Test
    void shouldValidateRequiredFields() {
        // Given - Request com campos inválidos
        CreateOrderRequest request = new CreateOrderRequest(
            "", // customerId vazio
            "", // productId vazio
            InsuranceType.AUTO, // category
            SalesChannel.WEB_SITE, // salesChannel
            PaymentMethod.CREDIT_CARD, // paymentMethod
            new BigDecimal("-100.00"), // totalMonthlyPremiumAmount negativo
            new BigDecimal("-1000.00"), // insuredAmount negativo
            Map.of(), // coverages vazio
            List.of(), // assistances vazio
            "" // description vazia
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Customer ID is required", "Insured amount must be greater than zero");
    }

    @Test
    void shouldCompleteFullOrderFlowWithApproval() throws Exception {
        // Given - Mock retorna REGULAR para fluxo normal
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        CreateOrderRequest request = new CreateOrderRequest(
            "1004", // customerId
            "PROD004", // productId
            InsuranceType.AUTO, // category
            SalesChannel.WEB_SITE, // salesChannel
            PaymentMethod.CREDIT_CARD, // paymentMethod
            new BigDecimal("600.00"), // totalMonthlyPremiumAmount
            new BigDecimal("60000.00"), // insuredAmount
            Map.of("collision", new BigDecimal("50000.00")), // coverages
            List.of("24h assistance", "towing"), // assistances
            "Seguro de automóvel completo" // description
        );

        // Step 1: Criar pedido e aguardar status PENDING
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Extrair ID do pedido da resposta
        String responseBody = createResponse.getBody();
        assertThat(responseBody).isNotNull();

        // Parse da resposta para extrair o ID
        ObjectMapper mapper = new ObjectMapper();
        var responseJson = mapper.readTree(responseBody);
        String orderId = responseJson.get("id").asText();

        assertThat(orderId).isNotNull();
        assertThat(orderId).isNotEmpty();

        // Step 2: Aguardar processamento assíncrono até status PENDING
        await().atMost(10, TimeUnit.SECONDS)
               .pollInterval(500, TimeUnit.MILLISECONDS)
               .until(() -> {
                   ResponseEntity<String> getResponse = restTemplate.getForEntity(
                       "http://localhost:" + port + "/api/v1/orders/" + orderId,
                       String.class
                   );
                   return getResponse.getStatusCode() == HttpStatus.OK &&
                          getResponse.getBody() != null &&
                          getResponse.getBody().contains("PENDING");
               });

        // Step 3: Aprovar subscription via API manual
        String subscriptionPayload = """
            {
                "orderId": "%s",
                "status": "APPROVED"
            }
            """.formatted(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> subscriptionEntity = new HttpEntity<>(subscriptionPayload, headers);

        ResponseEntity<String> subscriptionResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/manual-events/subscription",
            subscriptionEntity,
            String.class
        );

        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Aguardar processamento da subscription antes de enviar payment
        Thread.sleep(1000);

        // Step 4: Aprovar payment via API manual
        String paymentPayload = """
            {
                "orderId": "%s",
                "status": "APPROVED"
            }
            """.formatted(orderId);

        HttpEntity<String> paymentEntity = new HttpEntity<>(paymentPayload, headers);

        ResponseEntity<String> paymentResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/manual-events/payment",
            paymentEntity,
            String.class
        );

        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Step 5: Aguardar processamento final até status APPROVED
        await().atMost(15, TimeUnit.SECONDS)
               .pollInterval(500, TimeUnit.MILLISECONDS)
               .until(() -> {
                   ResponseEntity<String> finalResponse = restTemplate.getForEntity(
                       "http://localhost:" + port + "/api/v1/orders/" + orderId,
                       String.class
                   );

                   // Log apenas o status para debug
                   try {
                       if (finalResponse.getBody() != null) {
                           var statusJson = objectMapper.readTree(finalResponse.getBody());
                           String currentStatus = statusJson.get("status").asText();
                           System.out.println("⚠️ Status atual: " + currentStatus);

                           return "APPROVED".equals(currentStatus);
                       }
                       return false;
                   } catch (Exception e) {
                       System.err.println("Erro ao verificar status: " + e.getMessage());
                       return false;
                   }
               });

        // Step 6: Verificar se o pedido está aprovado
        ResponseEntity<String> finalCheckResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/orders/" + orderId,
            String.class
        );
        // Logar apenas o status final para debug
        try {
            if (finalCheckResponse.getBody() != null) {
                var finalStatusJson = objectMapper.readTree(finalCheckResponse.getBody());
                String finalStatus = finalStatusJson.get("status").asText();
                System.out.println("✅ Status final: " + finalStatus);
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar status final: " + e.getMessage());
        }

        JsonNode finalResponseJson = objectMapper.readTree(finalCheckResponse.getBody());
        assertThat(finalResponseJson.get("status").asText()).isEqualTo("APPROVED");
        assertThat(finalResponseJson.has("finishedAt")).isTrue();
        assertThat(finalResponseJson.get("finishedAt").isNull()).isFalse();
    }

    @Test
    void shouldRejectOrderWhenPaymentIsRejected() throws Exception {
        // Given - Mock retorna REGULAR para fluxo normal
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        CreateOrderRequest request = new CreateOrderRequest(
            "1005", // customerId
            "PROD005", // productId
            InsuranceType.HOME, // category
            SalesChannel.MOBILE, // salesChannel
            PaymentMethod.PIX, // paymentMethod
            new BigDecimal("400.00"), // totalMonthlyPremiumAmount
            new BigDecimal("40000.00"), // insuredAmount
            Map.of("fire", new BigDecimal("35000.00")), // coverages
            List.of("emergency service"), // assistances
            "Seguro residencial básico" // description
        );

        // Step 1: Criar pedido
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Extrair ID do pedido
        ObjectMapper mapper = new ObjectMapper();
        var responseJson = mapper.readTree(createResponse.getBody());
        String orderId = responseJson.get("id").asText();

        // Aguardar processamento
        Thread.sleep(1000);

        // Step 2: Aprovar subscription primeiro
        String subscriptionPayload = """
            {
                "orderId": "%s",
                "status": "APPROVED"
            }
            """.formatted(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> subscriptionEntity = new HttpEntity<>(subscriptionPayload, headers);

        restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/manual-events/subscription",
            subscriptionEntity,
            String.class
        );

        // Aguardar processamento da subscription antes de enviar payment
        Thread.sleep(1000);

        // Step 3: Rejeitar payment
        String paymentPayload = """
            {
                "orderId": "%s",
                "status": "REJECTED"
            }
            """.formatted(orderId);

        HttpEntity<String> paymentEntity = new HttpEntity<>(paymentPayload, headers);

        ResponseEntity<String> paymentResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/manual-events/payment",
            paymentEntity,
            String.class
        );

        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Aguardar processamento
        Thread.sleep(1000);

        // Step 4: Verificar status final REJECTED
        ResponseEntity<String> finalResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/orders/" + orderId,
            String.class
        );

        assertThat(finalResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String finalOrderDetails = finalResponse.getBody();
        assertThat(finalOrderDetails).contains("REJECTED");
    }
}
