package com.seguradora.msorder.application.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionEventDataTest {

    @Test
    void shouldCreateSubscriptionEventDataWithAllFields() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
        subscriptionDetails.setSubscriptionId("sub-123");
        subscriptionDetails.setAnalyst("John Analyst");

        // When
        eventData.setOrderId("order-456");
        eventData.setStatus("APPROVED");
        eventData.setReason("Subscription approved after risk analysis");
        eventData.setEventType("SUBSCRIPTION_APPROVED");
        eventData.setTimestamp("2025-01-15T10:30:00Z");
        eventData.setSubscriptionDetails(subscriptionDetails);

        // Then
        assertThat(eventData.getOrderId()).isEqualTo("order-456");
        assertThat(eventData.getStatus()).isEqualTo("APPROVED");
        assertThat(eventData.getReason()).isEqualTo("Subscription approved after risk analysis");
        assertThat(eventData.getEventType()).isEqualTo("SUBSCRIPTION_APPROVED");
        assertThat(eventData.getTimestamp()).isEqualTo("2025-01-15T10:30:00Z");
        assertThat(eventData.getSubscriptionDetails()).isEqualTo(subscriptionDetails);
    }

    @Test
    void shouldCreateSubscriptionEventDataWithNullValues() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();

        // When - não setamos nenhum valor

        // Then
        assertThat(eventData.getOrderId()).isNull();
        assertThat(eventData.getStatus()).isNull();
        assertThat(eventData.getReason()).isNull();
        assertThat(eventData.getEventType()).isNull();
        assertThat(eventData.getTimestamp()).isNull();
        assertThat(eventData.getSubscriptionDetails()).isNull();
    }

    @Test
    void shouldAllowSettingIndividualFields() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();

        // When
        eventData.setOrderId("order-123");
        eventData.setStatus("REJECTED");

        // Then
        assertThat(eventData.getOrderId()).isEqualTo("order-123");
        assertThat(eventData.getStatus()).isEqualTo("REJECTED");
        assertThat(eventData.getReason()).isNull();
        assertThat(eventData.getEventType()).isNull();
        assertThat(eventData.getTimestamp()).isNull();
        assertThat(eventData.getSubscriptionDetails()).isNull();
    }

    @Test
    void shouldAllowUpdatingFields() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();
        eventData.setStatus("PENDING");
        eventData.setReason("Under review");

        // When
        eventData.setStatus("APPROVED");
        eventData.setReason("Analysis completed successfully");

        // Then
        assertThat(eventData.getStatus()).isEqualTo("APPROVED");
        assertThat(eventData.getReason()).isEqualTo("Analysis completed successfully");
    }

    @Test
    void shouldHandleSubscriptionDetailsReference() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();
        SubscriptionDetails originalDetails = new SubscriptionDetails();
        originalDetails.setSubscriptionId("sub-001");
        originalDetails.setAnalyst("Analyst A");

        SubscriptionDetails newDetails = new SubscriptionDetails();
        newDetails.setSubscriptionId("sub-002");
        newDetails.setAnalyst("Analyst B");

        // When
        eventData.setSubscriptionDetails(originalDetails);
        assertThat(eventData.getSubscriptionDetails().getSubscriptionId()).isEqualTo("sub-001");

        eventData.setSubscriptionDetails(newDetails);

        // Then
        assertThat(eventData.getSubscriptionDetails().getSubscriptionId()).isEqualTo("sub-002");
        assertThat(eventData.getSubscriptionDetails().getAnalyst()).isEqualTo("Analyst B");
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();

        // When
        eventData.setOrderId("");
        eventData.setStatus("");
        eventData.setReason("");
        eventData.setEventType("");
        eventData.setTimestamp("");

        // Then
        assertThat(eventData.getOrderId()).isEmpty();
        assertThat(eventData.getStatus()).isEmpty();
        assertThat(eventData.getReason()).isEmpty();
        assertThat(eventData.getEventType()).isEmpty();
        assertThat(eventData.getTimestamp()).isEmpty();
    }

    @Test
    void shouldHandleTypicalSubscriptionEventScenarios() {
        // Given
        SubscriptionEventData approvedEvent = new SubscriptionEventData();
        SubscriptionEventData rejectedEvent = new SubscriptionEventData();

        // When - Cenário de aprovação
        approvedEvent.setOrderId("order-001");
        approvedEvent.setStatus("APPROVED");
        approvedEvent.setEventType("SUBSCRIPTION_APPROVED");
        approvedEvent.setReason("Risk analysis completed - low risk profile");

        // Cenário de rejeição
        rejectedEvent.setOrderId("order-002");
        rejectedEvent.setStatus("REJECTED");
        rejectedEvent.setEventType("SUBSCRIPTION_REJECTED");
        rejectedEvent.setReason("High risk factors identified during underwriting");

        // Then
        assertThat(approvedEvent.getStatus()).isEqualTo("APPROVED");
        assertThat(approvedEvent.getEventType()).isEqualTo("SUBSCRIPTION_APPROVED");
        assertThat(approvedEvent.getReason()).contains("low risk");

        assertThat(rejectedEvent.getStatus()).isEqualTo("REJECTED");
        assertThat(rejectedEvent.getEventType()).isEqualTo("SUBSCRIPTION_REJECTED");
        assertThat(rejectedEvent.getReason()).contains("High risk");
    }

    @Test
    void shouldHandleSubscriptionDetailsWithCompleteData() {
        // Given
        SubscriptionEventData eventData = new SubscriptionEventData();
        SubscriptionDetails details = new SubscriptionDetails();
        details.setSubscriptionId("sub-complete-001");
        details.setAnalyst("Senior Risk Analyst");
        details.setAnalysisDate("2025-01-15T14:30:00Z");
        details.setComments("Comprehensive risk assessment completed with favorable outcome");

        // When
        eventData.setOrderId("order-complete-001");
        eventData.setStatus("APPROVED");
        eventData.setEventType("SUBSCRIPTION_COMPLETED");
        eventData.setReason("Full underwriting process completed");
        eventData.setTimestamp("2025-01-15T15:00:00Z");
        eventData.setSubscriptionDetails(details);

        // Then
        assertThat(eventData.getSubscriptionDetails().getSubscriptionId()).contains("complete");
        assertThat(eventData.getSubscriptionDetails().getAnalyst()).contains("Senior");
        assertThat(eventData.getSubscriptionDetails().getComments()).contains("Comprehensive");
        assertThat(eventData.getEventType()).isEqualTo("SUBSCRIPTION_COMPLETED");
    }
}
