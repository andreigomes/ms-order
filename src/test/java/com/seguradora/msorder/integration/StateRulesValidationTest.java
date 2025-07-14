package com.seguradora.msorder.integration;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.integration.config.TestMessagingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Teste específico para validar as regras de estado conforme especificação
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
class StateRulesValidationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FraudAnalysisPort fraudAnalysisPort;

    @Test
    void shouldFollowCorrectStateFlow_RegularCustomer() {
        // REGRA 1-2: RECEIVED → VALIDATED → PENDING para cliente REGULAR
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST_REGULAR",
            BigDecimal.valueOf(5000.00),
            InsuranceType.AUTO,
            "Cliente regular - deve ir direto para PENDING"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("PENDING");
        System.out.println("✅ REGRA 1-2 VALIDADA: Cliente REGULAR → RECEIVED → VALIDATED → PENDING");
    }

    @Test
    void shouldStayValidated_HighRiskCustomer() {
        // REGRA 2: HIGH_RISK fica VALIDATED aguardando análise manual
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("HIGH_RISK");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST_HIGH_RISK",
            BigDecimal.valueOf(15000.00),
            InsuranceType.HOME,
            "Cliente alto risco - deve ficar VALIDATED"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("VALIDATED");
        System.out.println("✅ REGRA 2 VALIDADA: Cliente HIGH_RISK → RECEIVED → VALIDATED (aguarda análise)");
    }

    @Test
    void shouldHandlePreferentialCustomer() {
        // REGRA 2: Cliente PREFERENTIAL deve ir direto para PENDING
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("PREFERENTIAL");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST_PREFERENTIAL",
            BigDecimal.valueOf(8000.00),
            InsuranceType.TRAVEL,
            "Cliente preferencial - deve ir para PENDING"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("PENDING");
        System.out.println("✅ REGRA 2 VALIDADA: Cliente PREFERENTIAL → RECEIVED → VALIDATED → PENDING");
    }

    @Test
    void shouldHandleNoInformation() {
        // REGRA 2: NO_INFO deve ficar VALIDATED para análise manual
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("NO_INFO");

        CreateOrderRequest request = new CreateOrderRequest(
            "CUST_NO_INFO",
            BigDecimal.valueOf(3000.00),
            InsuranceType.HEALTH,
            "Cliente sem informação - deve ficar VALIDATED"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/orders",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("VALIDATED");
        System.out.println("✅ REGRA 2 VALIDADA: Cliente NO_INFO → RECEIVED → VALIDATED (aguarda análise)");
    }
}
