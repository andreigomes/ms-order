package com.seguradora.msorder.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudAnalysisPort fraudAnalysisPort;

    @Test
    void shouldCreateOrderWithLowRisk() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST001",
            InsuranceType.AUTO,
            new BigDecimal("800.00"), // Valor baixo para baixo risco
            "Seguro auto para veículo modelo 2023"
        );

        // Mock da API de fraudes para retornar baixo risco
        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("LOW");

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("CUST001"))
                .andExpect(jsonPath("$.insuranceType").value("AUTO"))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT")) // Status baseado no baixo risco
                .andExpect(jsonPath("$.amount").value(800.00))
                .andExpect(jsonPath("$.description").value("Seguro auto para veículo modelo 2023"));
    }

    @Test
    void shouldCreateOrderWithMediumRiskForAnalysis() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST002",
            InsuranceType.HOME,
            new BigDecimal("3000.00"), // Valor médio para risco médio
            "Seguro residencial"
        );

        // Mock da API de fraudes para retornar risco médio
        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("MEDIUM");

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("CUST002"))
                .andExpect(jsonPath("$.insuranceType").value("HOME"))
                .andExpect(jsonPath("$.status").value("PENDING_ANALYSIS")) // Status baseado no risco médio
                .andExpect(jsonPath("$.amount").value(3000.00));
    }

    @Test
    void shouldRejectOrderWhenCustomerIsBlocked() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "BLOCKED_CUSTOMER_001",
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Tentativa de pedido por cliente bloqueado"
        );

        // Mock da API de fraudes para retornar cliente bloqueado
        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Ainda cria o pedido, mas com status REJECTED
                .andExpect(jsonPath("$.customerId").value("BLOCKED_CUSTOMER_001"))
                .andExpect(jsonPath("$.status").value("REJECTED")) // Status rejeitado por cliente bloqueado
                .andExpect(jsonPath("$.amount").value(1500.00));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingOrderWithInvalidData() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "", // CustomerId vazio
            InsuranceType.AUTO,
            new BigDecimal("-100.00"), // Valor negativo
            "Description"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentOrder() throws Exception {
        // Given
        String nonExistentOrderId = "550e8400-e29b-41d4-a716-446655440000";

        // When & Then
        mockMvc.perform(get("/api/v1/orders/{orderId}", nonExistentOrderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetOrderSuccessfully() throws Exception {
        // Given - Primeiro criamos um pedido
        CreateOrderRequest createRequest = new CreateOrderRequest(
            "CUST003",
            InsuranceType.LIFE,
            new BigDecimal("1200.00"),
            "Seguro de vida"
        );

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any())).thenReturn("LOW");

        String response = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extrair o ID do pedido criado
        String orderId = objectMapper.readTree(response).get("id").asText();

        // When & Then - Buscamos o pedido criado
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.customerId").value("CUST003"))
                .andExpect(jsonPath("$.insuranceType").value("LIFE"))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }
}
