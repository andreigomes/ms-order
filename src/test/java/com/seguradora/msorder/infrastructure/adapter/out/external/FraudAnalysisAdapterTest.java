package com.seguradora.msorder.infrastructure.adapter.out.external;

import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FraudAnalysisAdapterTest {

    @Test
    void shouldInitializeAdapterCorrectly() {
        // Given
        String baseUrl = "http://localhost:8081";
        // When
        FraudAnalysisAdapter adapter = new FraudAnalysisAdapter(baseUrl);
        // Then
        assertThat(adapter).isNotNull();
    }

    @Test
    void shouldReturnRegularWhenWebClientThrowsException() {
        // Given
        String baseUrl = "http://localhost:8081";
        FraudAnalysisAdapter adapter = new FraudAnalysisAdapter(baseUrl);
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "order-123",
            "customer-123",
            new BigDecimal("100000"),
            "AUTO",
            "Test insurance"
        );
        // When
        String result = adapter.analyzeRisk(request);
        // Then
        assertThat(result).isEqualTo("REGULAR");
    }

    @Test
    void shouldCreateRequestCorrectly() {
        // Given
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "order-123",
            "customer-456",
            new BigDecimal("50000"),
            "HOME",
            "Home insurance policy"
        );

        // Then
        assertThat(request.orderId()).isEqualTo("order-123");
        assertThat(request.customerId()).isEqualTo("customer-456");
        assertThat(request.amount()).isEqualTo(new BigDecimal("50000"));
        assertThat(request.insuranceType()).isEqualTo("HOME");
        assertThat(request.description()).isEqualTo("Home insurance policy");
    }

    @Test
    void shouldHandleNullBaseUrl() {
        // When
        FraudAnalysisAdapter adapter = new FraudAnalysisAdapter(null);
        // Then
        assertThat(adapter).isNotNull();
    }
}
