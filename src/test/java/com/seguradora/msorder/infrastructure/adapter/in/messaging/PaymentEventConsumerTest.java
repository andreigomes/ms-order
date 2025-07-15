package com.seguradora.msorder.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.PaymentEventData;
import com.seguradora.msorder.core.usecase.coordination.EventCoordinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private EventCoordinationService coordinationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    private PaymentEventData approvedPaymentEvent;
    private PaymentEventData rejectedPaymentEvent;

    @BeforeEach
    void setUp() {
        approvedPaymentEvent = new PaymentEventData();
        approvedPaymentEvent.setOrderId("123e4567-e89b-12d3-a456-426614174000");
        approvedPaymentEvent.setStatus("APPROVED");
        approvedPaymentEvent.setReason("Payment processed successfully");

        rejectedPaymentEvent = new PaymentEventData();
        rejectedPaymentEvent.setOrderId("456e7890-e89b-12d3-a456-426614174000");
        rejectedPaymentEvent.setStatus("REJECTED");
        rejectedPaymentEvent.setReason("Insufficient funds");
    }

    @Test
    void shouldHandleApprovedPaymentEvent() throws Exception {
        // Given
        String messageJson = "{\"orderId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":\"APPROVED\",\"reason\":\"Payment processed successfully\"}";
        when(objectMapper.readValue(messageJson, PaymentEventData.class))
            .thenReturn(approvedPaymentEvent);

        // When
        paymentEventConsumer.handlePaymentEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, PaymentEventData.class);
        verify(coordinationService).processPaymentApproval("123e4567-e89b-12d3-a456-426614174000");
        verify(coordinationService, never()).processPaymentRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleRejectedPaymentEvent() throws Exception {
        // Given
        String messageJson = "{\"orderId\":\"456e7890-e89b-12d3-a456-426614174000\",\"status\":\"REJECTED\",\"reason\":\"Insufficient funds\"}";
        when(objectMapper.readValue(messageJson, PaymentEventData.class))
            .thenReturn(rejectedPaymentEvent);

        // When
        paymentEventConsumer.handlePaymentEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, PaymentEventData.class);
        verify(coordinationService).processPaymentRejection("456e7890-e89b-12d3-a456-426614174000", "Insufficient funds");
        verify(coordinationService, never()).processPaymentApproval(anyString());
    }

    @Test
    void shouldIgnoreUnknownPaymentStatus() throws Exception {
        // Given
        PaymentEventData unknownStatusEvent = new PaymentEventData();
        unknownStatusEvent.setOrderId("789e0123-e89b-12d3-a456-426614174000");
        unknownStatusEvent.setStatus("PENDING");
        unknownStatusEvent.setReason("Payment in progress");

        String messageJson = "{\"orderId\":\"789e0123-e89b-12d3-a456-426614174000\",\"status\":\"PENDING\",\"reason\":\"Payment in progress\"}";
        when(objectMapper.readValue(messageJson, PaymentEventData.class))
            .thenReturn(unknownStatusEvent);

        // When
        paymentEventConsumer.handlePaymentEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, PaymentEventData.class);
        verify(coordinationService, never()).processPaymentApproval(anyString());
        verify(coordinationService, never()).processPaymentRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleJsonParsingException() throws Exception {
        // Given
        String invalidJson = "invalid json";
        when(objectMapper.readValue(invalidJson, PaymentEventData.class))
            .thenThrow(new RuntimeException("JSON parsing error"));

        // When
        paymentEventConsumer.handlePaymentEvent(invalidJson);

        // Then
        verify(objectMapper).readValue(invalidJson, PaymentEventData.class);
        verify(coordinationService, never()).processPaymentApproval(anyString());
        verify(coordinationService, never()).processPaymentRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleCoordinationServiceException() throws Exception {
        // Given
        String messageJson = "{\"orderId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":\"APPROVED\",\"reason\":\"Payment processed successfully\"}";
        when(objectMapper.readValue(messageJson, PaymentEventData.class))
            .thenReturn(approvedPaymentEvent);
        doThrow(new RuntimeException("Coordination service error"))
            .when(coordinationService).processPaymentApproval(anyString());

        // When
        paymentEventConsumer.handlePaymentEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, PaymentEventData.class);
        verify(coordinationService).processPaymentApproval("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    void shouldHandleNullStatus() throws Exception {
        // Given
        PaymentEventData nullStatusEvent = new PaymentEventData();
        nullStatusEvent.setOrderId("123e4567-e89b-12d3-a456-426614174000");
        nullStatusEvent.setStatus(null);
        nullStatusEvent.setReason("No status");

        String messageJson = "{\"orderId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":null,\"reason\":\"No status\"}";
        when(objectMapper.readValue(messageJson, PaymentEventData.class))
            .thenReturn(nullStatusEvent);

        // When
        paymentEventConsumer.handlePaymentEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, PaymentEventData.class);
        verify(coordinationService, never()).processPaymentApproval(anyString());
        verify(coordinationService, never()).processPaymentRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleEmptyMessage() throws Exception {
        // Given
        String emptyMessage = "";
        when(objectMapper.readValue(emptyMessage, PaymentEventData.class))
            .thenThrow(new RuntimeException("Empty message"));

        // When
        paymentEventConsumer.handlePaymentEvent(emptyMessage);

        // Then
        verify(objectMapper).readValue(emptyMessage, PaymentEventData.class);
        verify(coordinationService, never()).processPaymentApproval(anyString());
        verify(coordinationService, never()).processPaymentRejection(anyString(), anyString());
    }
}
