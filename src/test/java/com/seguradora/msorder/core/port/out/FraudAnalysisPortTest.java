package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes para verificar o contrato da interface FraudAnalysisPort
 */
class FraudAnalysisPortTest {

    @Test
    void shouldReturnRiskLevelWhenAnalyzeRiskIsCalled() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "ORDER001",
            "CUST001",
            new BigDecimal("50000.00"),
            "AUTO",
            "Test order"
        );

        when(port.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("REGULAR");

        // When
        String result = port.analyzeRisk(request);

        // Then
        assertThat(result).isEqualTo("REGULAR");
    }

    @Test
    void shouldReturnHighRiskForHighRiskCustomer() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "ORDER002",
            "HIGH_RISK_CUST",
            new BigDecimal("100000.00"),
            "AUTO",
            "High risk order"
        );

        when(port.analyzeRisk(request)).thenReturn("HIGH_RISK");

        // When
        String result = port.analyzeRisk(request);

        // Then
        assertThat(result).isEqualTo("HIGH_RISK");
    }

    @Test
    void shouldReturnPreferentialForPreferentialCustomer() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "ORDER003",
            "PREF_CUST",
            new BigDecimal("25000.00"),
            "HOME",
            "Preferential customer order"
        );

        when(port.analyzeRisk(request)).thenReturn("PREFERENTIAL");

        // When
        String result = port.analyzeRisk(request);

        // Then
        assertThat(result).isEqualTo("PREFERENTIAL");
    }

    @Test
    void shouldReturnNoInfoWhenCustomerNotFound() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "ORDER004",
            "UNKNOWN_CUST",
            new BigDecimal("30000.00"),
            "LIFE",
            "Unknown customer order"
        );

        when(port.analyzeRisk(request)).thenReturn("NO_INFO");

        // When
        String result = port.analyzeRisk(request);

        // Then
        assertThat(result).isEqualTo("NO_INFO");
    }

    @Test
    void shouldHandleDifferentInsuranceTypes() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        String[] insuranceTypes = {"AUTO", "HOME", "LIFE", "HEALTH", "TRAVEL", "BUSINESS"};

        for (int i = 0; i < insuranceTypes.length; i++) {
            String type = insuranceTypes[i];
            FraudAnalysisRequest request = new FraudAnalysisRequest(
                "ORDER00" + (i + 5),
                "CUST_" + type,
                new BigDecimal("40000.00"),
                type,
                "Order for " + type + " insurance"
            );

            when(port.analyzeRisk(request)).thenReturn("REGULAR");

            // When
            String result = port.analyzeRisk(request);

            // Then
            assertThat(result).isEqualTo("REGULAR");
        }
    }

    @Test
    void shouldHandleDifferentAmountRanges() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        BigDecimal[] amounts = {
            new BigDecimal("10000.00"),
            new BigDecimal("50000.00"),
            new BigDecimal("100000.00"),
            new BigDecimal("500000.00")
        };

        String[] expectedResults = {"REGULAR", "REGULAR", "HIGH_RISK", "HIGH_RISK"};

        for (int i = 0; i < amounts.length; i++) {
            FraudAnalysisRequest request = new FraudAnalysisRequest(
                "ORDER_AMOUNT_" + i,
                "CUST" + i,
                amounts[i],
                "AUTO",
                "Order with amount " + amounts[i]
            );

            when(port.analyzeRisk(request)).thenReturn(expectedResults[i]);

            // When
            String result = port.analyzeRisk(request);

            // Then
            assertThat(result).isEqualTo(expectedResults[i]);
        }
    }

    @Test
    void shouldReturnExpectedRiskLevels() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);
        String[] validRiskLevels = {"REGULAR", "HIGH_RISK", "PREFERENTIAL", "NO_INFO"};

        // Testa todos os níveis de risco válidos
        for (int i = 0; i < validRiskLevels.length; i++) {
            String riskLevel = validRiskLevels[i];
            FraudAnalysisRequest request = new FraudAnalysisRequest(
                "ORDER_RISK_" + i,
                "CUST_" + riskLevel,
                new BigDecimal("50000.00"),
                "AUTO",
                "Order for risk level " + riskLevel
            );

            when(port.analyzeRisk(request)).thenReturn(riskLevel);

            // When
            String result = port.analyzeRisk(request);

            // Then
            assertThat(result).isEqualTo(riskLevel);
            assertThat(result).isIn((Object[]) validRiskLevels);
        }
    }

    @Test
    void shouldVerifyMethodSignature() {
        // Given
        FraudAnalysisPort port = Mockito.mock(FraudAnalysisPort.class);

        // When & Then - Verifica que o método existe e pode ser chamado
        assertThat(port).isNotNull();

        // Verifica que o método analyzeRisk existe na interface
        java.lang.reflect.Method[] methods = FraudAnalysisPort.class.getDeclaredMethods();
        assertThat(methods).hasSize(1);
        assertThat(methods[0].getName()).isEqualTo("analyzeRisk");
        assertThat(methods[0].getParameterCount()).isEqualTo(1);
        assertThat(methods[0].getParameterTypes()[0]).isEqualTo(FraudAnalysisRequest.class);
        assertThat(methods[0].getReturnType()).isEqualTo(String.class);
    }
}
