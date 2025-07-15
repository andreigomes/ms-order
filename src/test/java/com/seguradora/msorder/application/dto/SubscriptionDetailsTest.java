package com.seguradora.msorder.application.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionDetailsTest {

    @Test
    void shouldCreateSubscriptionDetailsWithAllFields() {
        // Given
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
        String subscriptionId = "sub-123";
        String analyst = "John Smith";
        String analysisDate = "2025-01-15T14:30:00Z";
        String comments = "Policy approved after thorough risk analysis";

        // When
        subscriptionDetails.setSubscriptionId(subscriptionId);
        subscriptionDetails.setAnalyst(analyst);
        subscriptionDetails.setAnalysisDate(analysisDate);
        subscriptionDetails.setComments(comments);

        // Then
        assertThat(subscriptionDetails.getSubscriptionId()).isEqualTo(subscriptionId);
        assertThat(subscriptionDetails.getAnalyst()).isEqualTo(analyst);
        assertThat(subscriptionDetails.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(subscriptionDetails.getComments()).isEqualTo(comments);
    }

    @Test
    void shouldCreateSubscriptionDetailsWithNullValues() {
        // Given
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();

        // When - não setamos nenhum valor

        // Then
        assertThat(subscriptionDetails.getSubscriptionId()).isNull();
        assertThat(subscriptionDetails.getAnalyst()).isNull();
        assertThat(subscriptionDetails.getAnalysisDate()).isNull();
        assertThat(subscriptionDetails.getComments()).isNull();
    }

    @Test
    void shouldAllowSettingIndividualFields() {
        // Given
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();

        // When
        subscriptionDetails.setSubscriptionId("sub-001");
        subscriptionDetails.setAnalyst("Jane Doe");

        // Then
        assertThat(subscriptionDetails.getSubscriptionId()).isEqualTo("sub-001");
        assertThat(subscriptionDetails.getAnalyst()).isEqualTo("Jane Doe");
        assertThat(subscriptionDetails.getAnalysisDate()).isNull();
        assertThat(subscriptionDetails.getComments()).isNull();
    }

    @Test
    void shouldAllowUpdatingFields() {
        // Given
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
        subscriptionDetails.setAnalyst("Initial Analyst");
        subscriptionDetails.setComments("Initial comments");

        // When
        subscriptionDetails.setAnalyst("Updated Analyst");
        subscriptionDetails.setComments("Updated analysis comments");

        // Then
        assertThat(subscriptionDetails.getAnalyst()).isEqualTo("Updated Analyst");
        assertThat(subscriptionDetails.getComments()).isEqualTo("Updated analysis comments");
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();

        // When
        subscriptionDetails.setSubscriptionId("");
        subscriptionDetails.setAnalyst("");
        subscriptionDetails.setAnalysisDate("");
        subscriptionDetails.setComments("");

        // Then
        assertThat(subscriptionDetails.getSubscriptionId()).isEmpty();
        assertThat(subscriptionDetails.getAnalyst()).isEmpty();
        assertThat(subscriptionDetails.getAnalysisDate()).isEmpty();
        assertThat(subscriptionDetails.getComments()).isEmpty();
    }

    @Test
    void shouldHandleTypicalSubscriptionScenarios() {
        // Given
        SubscriptionDetails approvedSubscription = new SubscriptionDetails();
        SubscriptionDetails rejectedSubscription = new SubscriptionDetails();

        // When - Cenário de aprovação
        approvedSubscription.setSubscriptionId("sub-approved-001");
        approvedSubscription.setAnalyst("Senior Analyst");
        approvedSubscription.setAnalysisDate("2025-01-15T10:00:00Z");
        approvedSubscription.setComments("Low risk profile, approved for full coverage");

        // Cenário de rejeição
        rejectedSubscription.setSubscriptionId("sub-rejected-002");
        rejectedSubscription.setAnalyst("Risk Analyst");
        rejectedSubscription.setAnalysisDate("2025-01-15T11:00:00Z");
        rejectedSubscription.setComments("High risk factors identified, subscription rejected");

        // Then
        assertThat(approvedSubscription.getSubscriptionId()).contains("approved");
        assertThat(approvedSubscription.getComments()).contains("approved");
        assertThat(approvedSubscription.getAnalyst()).isEqualTo("Senior Analyst");

        assertThat(rejectedSubscription.getSubscriptionId()).contains("rejected");
        assertThat(rejectedSubscription.getComments()).contains("rejected");
        assertThat(rejectedSubscription.getAnalyst()).isEqualTo("Risk Analyst");
    }

    @Test
    void shouldHandleLongComments() {
        // Given
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
        String longComment = "This is a very detailed analysis that includes multiple factors: " +
                "customer credit history, previous claims, risk assessment scores, " +
                "geographical factors, and additional underwriting considerations " +
                "that led to this subscription decision.";

        // When
        subscriptionDetails.setComments(longComment);

        // Then
        assertThat(subscriptionDetails.getComments()).isEqualTo(longComment);
        assertThat(subscriptionDetails.getComments().length()).isGreaterThan(100);
    }
}
