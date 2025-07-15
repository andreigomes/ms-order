package com.seguradora.msorder.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.SubscriptionEventData;
import com.seguradora.msorder.core.usecase.coordination.EventCoordinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionEventConsumerTest {

    @Mock
    private EventCoordinationService coordinationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SubscriptionEventConsumer subscriptionEventConsumer;

    private SubscriptionEventData approvedSubscriptionEvent;
    private SubscriptionEventData rejectedSubscriptionEvent;

    @BeforeEach
    void setUp() {
        approvedSubscriptionEvent = new SubscriptionEventData();
        approvedSubscriptionEvent.setOrderId("123e4567-e89b-12d3-a456-426614174000");
        approvedSubscriptionEvent.setStatus("APPROVED");
        approvedSubscriptionEvent.setReason("Subscription approved after analysis");

        rejectedSubscriptionEvent = new SubscriptionEventData();
        rejectedSubscriptionEvent.setOrderId("456e7890-e89b-12d3-a456-426614174000");
        rejectedSubscriptionEvent.setStatus("REJECTED");
        rejectedSubscriptionEvent.setReason("High risk customer");
    }

    @Test
    void shouldHandleApprovedSubscriptionEvent() throws Exception {
        // Given
        String messageJson = "{\"orderId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":\"APPROVED\",\"reason\":\"Subscription approved after analysis\"}";
        when(objectMapper.readValue(messageJson, SubscriptionEventData.class))
            .thenReturn(approvedSubscriptionEvent);

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, SubscriptionEventData.class);
        verify(coordinationService).processSubscriptionApproval("123e4567-e89b-12d3-a456-426614174000");
        verify(coordinationService, never()).processSubscriptionRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleRejectedSubscriptionEvent() throws Exception {
        // Given
        String messageJson = "{\"orderId\":\"456e7890-e89b-12d3-a456-426614174000\",\"status\":\"REJECTED\",\"reason\":\"High risk customer\"}";
        when(objectMapper.readValue(messageJson, SubscriptionEventData.class))
            .thenReturn(rejectedSubscriptionEvent);

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, SubscriptionEventData.class);
        verify(coordinationService).processSubscriptionRejection("456e7890-e89b-12d3-a456-426614174000", "High risk customer");
        verify(coordinationService, never()).processSubscriptionApproval(anyString());
    }

    @Test
    void shouldIgnoreUnknownSubscriptionStatus() throws Exception {
        // Given
        SubscriptionEventData unknownStatusEvent = new SubscriptionEventData();
        unknownStatusEvent.setOrderId("789e0123-e89b-12d3-a456-426614174000");
        unknownStatusEvent.setStatus("PENDING");
        unknownStatusEvent.setReason("Analysis in progress");

        String messageJson = "{\"orderId\":\"789e0123-e89b-12d3-a456-426614174000\",\"status\":\"PENDING\",\"reason\":\"Analysis in progress\"}";
        when(objectMapper.readValue(messageJson, SubscriptionEventData.class))
            .thenReturn(unknownStatusEvent);

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, SubscriptionEventData.class);
        verify(coordinationService, never()).processSubscriptionApproval(anyString());
        verify(coordinationService, never()).processSubscriptionRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleJsonParsingException() throws Exception {
        // Given
        String invalidJson = "invalid json";
        when(objectMapper.readValue(invalidJson, SubscriptionEventData.class))
            .thenThrow(new RuntimeException("JSON parsing error"));

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(invalidJson);

        // Then
        verify(objectMapper).readValue(invalidJson, SubscriptionEventData.class);
        verify(coordinationService, never()).processSubscriptionApproval(anyString());
        verify(coordinationService, never()).processSubscriptionRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleCoordinationServiceException() throws Exception {
        // Given
        String messageJson = "{\"orderId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":\"APPROVED\",\"reason\":\"Subscription approved after analysis\"}";
        when(objectMapper.readValue(messageJson, SubscriptionEventData.class))
            .thenReturn(approvedSubscriptionEvent);
        doThrow(new RuntimeException("Coordination service error"))
            .when(coordinationService).processSubscriptionApproval(anyString());

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, SubscriptionEventData.class);
        verify(coordinationService).processSubscriptionApproval("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    void shouldHandleNullStatus() throws Exception {
        // Given
        SubscriptionEventData nullStatusEvent = new SubscriptionEventData();
        nullStatusEvent.setOrderId("123e4567-e89b-12d3-a456-426614174000");
        nullStatusEvent.setStatus(null);
        nullStatusEvent.setReason("No status");

        String messageJson = "{\"orderId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":null,\"reason\":\"No status\"}";
        when(objectMapper.readValue(messageJson, SubscriptionEventData.class))
            .thenReturn(nullStatusEvent);

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(messageJson);

        // Then
        verify(objectMapper).readValue(messageJson, SubscriptionEventData.class);
        verify(coordinationService, never()).processSubscriptionApproval(anyString());
        verify(coordinationService, never()).processSubscriptionRejection(anyString(), anyString());
    }

    @Test
    void shouldHandleEmptyMessage() throws Exception {
        // Given
        String emptyMessage = "";
        when(objectMapper.readValue(emptyMessage, SubscriptionEventData.class))
            .thenThrow(new RuntimeException("Empty message"));

        // When
        subscriptionEventConsumer.handleSubscriptionEvent(emptyMessage);

        // Then
        verify(objectMapper).readValue(emptyMessage, SubscriptionEventData.class);
        verify(coordinationService, never()).processSubscriptionApproval(anyString());
        verify(coordinationService, never()).processSubscriptionRejection(anyString(), anyString());
    }
}
