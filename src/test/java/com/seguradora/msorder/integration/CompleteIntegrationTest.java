package com.seguradora.msorder.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
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

import com.seguradora.msorder.integration.config.TestMessagingConfig;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Teste de integração sem dependência de Docker
 * Testa o fluxo da API REST com mocks para evitar problemas de Kafka
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
class CompleteIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FraudAnalysisPort fraudAnalysisPort;

    @Test
    void shouldCreateOrderSuccessfully() {
        // Given - Mock retorna classificação REGULAR para fluxo normal
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST001",
            BigDecimal.valueOf(5000.00),
            InsuranceType.AUTO,
            "Seguro de automóvel"
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
        assertThat(response.getBody()).contains("PENDING"); // Estado final após validação
    }

    @Test
    void shouldRejectOrderForHighRisk() {
        // Given - Mock retorna BLOCKED para cliente de alto risco
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("BLOCKED");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST002",
            BigDecimal.valueOf(10000.00),
            InsuranceType.AUTO,
            "Seguro de automóvel"
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
        assertThat(response.getBody()).contains("REJECTED"); // Deve ser rejeitado
    }

    @Test
    void shouldValidateButRequireAnalysisForHighRisk() {
        // Given - Mock retorna ALTO_RISCO
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("ALTO_RISCO");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST003",
            BigDecimal.valueOf(15000.00),
            InsuranceType.HOME,
            "Seguro residencial"
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
        assertThat(response.getBody()).contains("VALIDATED"); // Deve ficar validado aguardando análise
    }

    @Test
    void shouldBlockOrderForBlockedCustomer() {
        // Given - Cliente bloqueado diretamente

        CreateOrderRequest request = new CreateOrderRequest(
            "BLOCKED_CUSTOMER",
            BigDecimal.valueOf(5000.00),
            InsuranceType.AUTO,
            "Seguro de automóvel"
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
        assertThat(response.getBody()).contains("REJECTED"); // Deve ser rejeitado direto
    }

    @Test
    void shouldReturnBadRequestForInvalidOrder() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            null, // customerId inválido
            BigDecimal.valueOf(-100), // valor inválido
            InsuranceType.AUTO,
            ""
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
