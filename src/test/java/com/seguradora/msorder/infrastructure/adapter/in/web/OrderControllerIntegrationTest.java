package com.seguradora.msorder.infrastructure.adapter.in.web;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.integration.config.TestKafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Import(TestKafkaConfig.class)
class OrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FraudAnalysisPort fraudAnalysisPort;

    @Test
    void shouldCreateOrderWithLowRisk() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST001",
            new BigDecimal("800.00"), // Valor baixo para baixo risco
            InsuranceType.AUTO,
            "Seguro auto para veículo modelo 2023"
        );

        // Mock da API de fraudes para retornar baixo risco
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("LOW");

        // When & Then
        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response.customerId()).isEqualTo("CUST001");
                    assertThat(response.insuranceType()).isEqualTo(InsuranceType.AUTO);
                    assertThat(response.status().name()).isEqualTo("PENDING");
                    assertThat(response.amount()).isEqualTo(new BigDecimal("800.00"));
                    assertThat(response.description()).isEqualTo("Seguro auto para veículo modelo 2023");
                    assertThat(response.id()).isNotNull();
                    assertThat(response.createdAt()).isNotNull();
                });
    }

    @Test
    void shouldCreateOrderWithMediumRiskForAnalysis() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST002",
            new BigDecimal("3000.00"), // Valor médio para risco médio
            InsuranceType.HOME,
            "Seguro residencial"
        );

        // Mock da API de fraudes para retornar risco médio
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("ALTO_RISCO");

        // When & Then
        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response.customerId()).isEqualTo("CUST002");
                    assertThat(response.insuranceType()).isEqualTo(InsuranceType.HOME);
                    assertThat(response.status().name()).isEqualTo("VALIDATED");
                    assertThat(response.amount()).isEqualTo(new BigDecimal("3000.00"));
                });
    }

    @Test
    void shouldRejectOrderWhenCustomerIsBlocked() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "BLOCKED_CUSTOMER_001",
            new BigDecimal("1500.00"),
            InsuranceType.AUTO,
            "Tentativa de pedido por cliente bloqueado"
        );

        // Mock da API de fraudes para retornar cliente bloqueado

        // When & Then
        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated() // Ainda cria o pedido, mas com status REJECTED
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response.customerId()).isEqualTo("BLOCKED_CUSTOMER_001");
                    assertThat(response.status().name()).isEqualTo("REJECTED");
                    assertThat(response.amount()).isEqualTo(new BigDecimal("1500.00"));
                });
    }

    @Test
    void shouldReturnBadRequestWhenCreatingOrderWithInvalidData() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "", // CustomerId vazio
            new BigDecimal("-100.00"), // Valor negativo
            InsuranceType.AUTO,
            "Description"
        );

        // When & Then
        webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentOrder() {
        // Given
        String nonExistentOrderId = "550e8400-e29b-41d4-a716-446655440000";

        // When & Then
        webTestClient.get()
                .uri("/api/v1/orders/{orderId}", nonExistentOrderId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldGetOrderSuccessfully() {
        // Given - Primeiro criamos um pedido
        CreateOrderRequest createRequest = new CreateOrderRequest(
            "CUST003",
            new BigDecimal("1200.00"),
            InsuranceType.LIFE,
            "Seguro de vida"
        );

        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        // Criar pedido e obter o ID
        OrderResponse createdOrder = webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdOrder).isNotNull();
        String orderId = createdOrder.id();

        // When & Then - Buscamos o pedido criado
        webTestClient.get()
                .uri("/api/v1/orders/{orderId}", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(orderId);
                    assertThat(response.customerId()).isEqualTo("CUST003");
                    assertThat(response.insuranceType()).isEqualTo(InsuranceType.LIFE);
                    assertThat(response.status().name()).isEqualTo("PENDING");
                });
    }

    @Test
    void shouldGetOrdersByCustomerId() {
        // Given - Criar dois pedidos para o mesmo cliente
        String customerId = "CUST004";

        CreateOrderRequest request1 = new CreateOrderRequest(
            customerId,
            new BigDecimal("1000.00"),
            InsuranceType.AUTO,
            "Seguro auto"
        );

        CreateOrderRequest request2 = new CreateOrderRequest(
            customerId,
            new BigDecimal("2000.00"),
            InsuranceType.HOME,
            "Seguro residencial"
        );

        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        // Criar os pedidos
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

        // When & Then - Buscar pedidos por cliente
        webTestClient.get()
                .uri("/api/v1/orders/customer/{customerId}", customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderResponse.class)
                .value(orders -> {
                    assertThat(orders).hasSize(2);
                    assertThat(orders).allMatch(order -> order.customerId().equals(customerId));
                });
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        // Given - Criar um pedido e colocá-lo em PENDING
        CreateOrderRequest createRequest = new CreateOrderRequest(
            "CUST005",
            new BigDecimal("800.00"),
            InsuranceType.AUTO,
            "Seguro auto"
        );

        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("REGULAR");

        OrderResponse createdOrder = webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdOrder).isNotNull();
        String orderId = createdOrder.id();

        // Verificar que está em PENDING
        assertThat(createdOrder.status().name()).isEqualTo("PENDING");

        // When & Then - Aprovar o pedido diretamente
        webTestClient.put()
                .uri("/api/v1/orders/{orderId}/approve", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(orderId);
                    assertThat(response.status().name()).isEqualTo("APPROVED");
                });
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        // Given - Criar um pedido em status que permite cancelamento
        CreateOrderRequest createRequest = new CreateOrderRequest(
            "CUST006",
            new BigDecimal("1500.00"),
            InsuranceType.LIFE,
            "Seguro de vida"
        );

        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("ALTO_RISCO"); // Para ficar em VALIDATED

        OrderResponse createdOrder = webTestClient.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdOrder).isNotNull();
        String orderId = createdOrder.id();

        // When & Then - Cancelar pedido
        webTestClient.put()
                .uri("/api/v1/orders/{orderId}/cancel", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(orderId);
                    assertThat(response.status().name()).isEqualTo("CANCELLED");
                });
    }
}
