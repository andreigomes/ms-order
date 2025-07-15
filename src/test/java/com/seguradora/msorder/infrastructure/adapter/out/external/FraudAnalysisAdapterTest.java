package com.seguradora.msorder.infrastructure.adapter.out.external;

import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudAnalysisAdapterTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Test
    void shouldInitializeAdapterCorrectly() {
        // Given
        String baseUrl = "http://localhost:8081";
        when(webClientBuilder.baseUrl(baseUrl)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        FraudAnalysisAdapter adapter = new FraudAnalysisAdapter(webClientBuilder, baseUrl);

        // Then
        assertThat(adapter).isNotNull();
        verify(webClientBuilder).baseUrl(baseUrl);
        verify(webClientBuilder).build();
    }

    @Test
    void shouldReturnRegularWhenWebClientThrowsException() {
        // Given
        String baseUrl = "http://localhost:8081";
        when(webClientBuilder.baseUrl(baseUrl)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        FraudAnalysisAdapter adapter = new FraudAnalysisAdapter(webClientBuilder, baseUrl);
        FraudAnalysisRequest request = new FraudAnalysisRequest(
            "order-123",
            "customer-123",
            new BigDecimal("100000"),
            "AUTO",
            "Test insurance"
        );

        // Mock para simular exceção no webClient.post()
        when(webClient.post()).thenThrow(new RuntimeException("Connection failed"));

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
        // Given
        when(webClientBuilder.baseUrl(null)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        // When
        FraudAnalysisAdapter adapter = new FraudAnalysisAdapter(webClientBuilder, null);

        // Then
        assertThat(adapter).isNotNull();
        verify(webClientBuilder).baseUrl(null);
        verify(webClientBuilder).build();
    }
}
