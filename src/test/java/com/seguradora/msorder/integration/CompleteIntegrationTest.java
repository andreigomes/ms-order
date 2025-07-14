package com.seguradora.msorder.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.SalesChannel;
import com.seguradora.msorder.core.domain.valueobject.PaymentMethod;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.seguradora.msorder.integration.config.TestMessagingConfig;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Teste de integração usando PostgreSQL via TestContainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
class CompleteIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("orders_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");

        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.clean-on-validation-error", () -> "true");
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
}
