package com.seguradora.msorder.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.ManualPaymentEventRequest;
import com.seguradora.msorder.application.dto.ManualSubscriptionEventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManualEventController.class)
class ManualEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldPublishPaymentEventSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualPaymentEventRequest request = new ManualPaymentEventRequest(
            orderId,
            ManualPaymentEventRequest.PaymentStatus.APPROVED,
            "Payment processed successfully"
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("payment-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("payment-events"), eq(orderId), anyString());
    }

    @Test
    void shouldPublishPaymentEventWithoutReason() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualPaymentEventRequest request = new ManualPaymentEventRequest(
            orderId,
            ManualPaymentEventRequest.PaymentStatus.APPROVED,
            null
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("payment-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("payment-events"), eq(orderId), anyString());
    }

    @Test
    void shouldPublishRejectedPaymentEvent() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualPaymentEventRequest request = new ManualPaymentEventRequest(
            orderId,
            ManualPaymentEventRequest.PaymentStatus.REJECTED,
            "Insufficient funds"
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("payment-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("payment-events"), eq(orderId), anyString());
    }

    @Test
    void shouldPublishSubscriptionEventSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualSubscriptionEventRequest request = new ManualSubscriptionEventRequest(
            orderId,
            ManualSubscriptionEventRequest.SubscriptionStatus.APPROVED,
            "Subscription approved after review"
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("subscription-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("subscription-events"), eq(orderId), anyString());
    }

    @Test
    void shouldPublishSubscriptionEventWithoutReason() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualSubscriptionEventRequest request = new ManualSubscriptionEventRequest(
            orderId,
            ManualSubscriptionEventRequest.SubscriptionStatus.APPROVED,
            null
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("subscription-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("subscription-events"), eq(orderId), anyString());
    }

    @Test
    void shouldPublishRejectedSubscriptionEvent() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualSubscriptionEventRequest request = new ManualSubscriptionEventRequest(
            orderId,
            ManualSubscriptionEventRequest.SubscriptionStatus.REJECTED,
            "High risk customer"
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("subscription-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("subscription-events"), eq(orderId), anyString());
    }

    @Test
    void shouldHandlePaymentEventKafkaException() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualPaymentEventRequest request = new ManualPaymentEventRequest(
            orderId,
            ManualPaymentEventRequest.PaymentStatus.APPROVED,
            "Payment processed"
        );

        when(kafkaTemplate.send(eq("payment-events"), eq(orderId), anyString()))
            .thenThrow(new RuntimeException("Kafka connection failed"));

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error publishing payment event: Kafka connection failed"));
    }

    @Test
    void shouldHandleSubscriptionEventKafkaException() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualSubscriptionEventRequest request = new ManualSubscriptionEventRequest(
            orderId,
            ManualSubscriptionEventRequest.SubscriptionStatus.REJECTED,
            "Analysis failed"
        );

        when(kafkaTemplate.send(eq("subscription-events"), eq(orderId), anyString()))
            .thenThrow(new RuntimeException("Network timeout"));

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error publishing subscription event: Network timeout"));
    }

    @Test
    void shouldReturnAvailableTopicsInformation() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/manual-events/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_topic").value("payment-events"))
                .andExpect(jsonPath("$.subscription_topic").value("subscription-events"))
                .andExpect(jsonPath("$.message_format").value("orderId:status:reason"))
                .andExpect(jsonPath("$.available_statuses.payment[0]").value("APPROVED"))
                .andExpect(jsonPath("$.available_statuses.payment[1]").value("REJECTED"))
                .andExpect(jsonPath("$.available_statuses.subscription[0]").value("APPROVED"))
                .andExpect(jsonPath("$.available_statuses.subscription[1]").value("REJECTED"))
                .andExpect(jsonPath("$.example_usage.payment.orderId").exists())
                .andExpect(jsonPath("$.example_usage.payment.status").value("APPROVED"))
                .andExpect(jsonPath("$.example_usage.subscription.orderId").exists())
                .andExpect(jsonPath("$.example_usage.subscription.status").value("APPROVED"));
    }

    @Test
    void shouldValidatePaymentEventRequest() throws Exception {
        // Given - Request com campos obrigatórios nulos
        ManualPaymentEventRequest invalidRequest = new ManualPaymentEventRequest(
            null, // orderId é obrigatório
            null, // status é obrigatório
            "reason"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateSubscriptionEventRequest() throws Exception {
        // Given - Request com campos obrigatórios nulos
        ManualSubscriptionEventRequest invalidRequest = new ManualSubscriptionEventRequest(
            null, // orderId é obrigatório
            null, // status é obrigatório
            "reason"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptValidPaymentEventWithRejectedStatus() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualPaymentEventRequest request = new ManualPaymentEventRequest(
            orderId,
            ManualPaymentEventRequest.PaymentStatus.REJECTED,
            "Insufficient funds"
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("payment-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("payment-events"), eq(orderId), contains("REJECTED"));
    }

    @Test
    void shouldAcceptValidSubscriptionEventWithRejectedStatus() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        ManualSubscriptionEventRequest request = new ManualSubscriptionEventRequest(
            orderId,
            ManualSubscriptionEventRequest.SubscriptionStatus.REJECTED,
            "High risk customer"
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("subscription-events"), eq(orderId), anyString()))
            .thenReturn(future);

        // When & Then
        mockMvc.perform(post("/api/v1/manual-events/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription event published successfully for order: " + orderId));

        verify(kafkaTemplate).send(eq("subscription-events"), eq(orderId), contains("REJECTED"));
    }
}
