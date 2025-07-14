package com.seguradora.msorder.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalServicesSimulatorPropertiesTest {

    @Test
    void shouldCreatePropertiesWithDefaultValues() {
        // When
        ExternalServicesSimulatorProperties properties = new ExternalServicesSimulatorProperties();

        // Then
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getFixedPaymentStatus()).isNull();
        assertThat(properties.getFixedSubscriptionStatus()).isNull();
        assertThat(properties.getPaymentDelaySeconds()).isEqualTo(2);
        assertThat(properties.getSubscriptionDelaySeconds()).isEqualTo(3);
    }

    @Test
    void shouldSetAndGetPaymentProperties() {
        // Given
        ExternalServicesSimulatorProperties properties = new ExternalServicesSimulatorProperties();

        // When
        properties.setEnabled(true);
        properties.setFixedPaymentStatus("APPROVED");
        properties.setPaymentDelaySeconds(5);

        // Then
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getFixedPaymentStatus()).isEqualTo("APPROVED");
        assertThat(properties.getPaymentDelaySeconds()).isEqualTo(5);
    }

    @Test
    void shouldSetAndGetSubscriptionProperties() {
        // Given
        ExternalServicesSimulatorProperties properties = new ExternalServicesSimulatorProperties();

        // When
        properties.setEnabled(true);
        properties.setFixedSubscriptionStatus("REJECTED");
        properties.setSubscriptionDelaySeconds(10);

        // Then
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getFixedSubscriptionStatus()).isEqualTo("REJECTED");
        assertThat(properties.getSubscriptionDelaySeconds()).isEqualTo(10);
    }

    @Test
    void shouldSetAllProperties() {
        // Given
        ExternalServicesSimulatorProperties properties = new ExternalServicesSimulatorProperties();

        // When
        properties.setEnabled(true);
        properties.setFixedPaymentStatus("APPROVED");
        properties.setFixedSubscriptionStatus("APPROVED");
        properties.setPaymentDelaySeconds(1);
        properties.setSubscriptionDelaySeconds(2);

        // Then
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getFixedPaymentStatus()).isEqualTo("APPROVED");
        assertThat(properties.getFixedSubscriptionStatus()).isEqualTo("APPROVED");
        assertThat(properties.getPaymentDelaySeconds()).isEqualTo(1);
        assertThat(properties.getSubscriptionDelaySeconds()).isEqualTo(2);
    }

    @Test
    void shouldHandleNullFixedStatuses() {
        // Given
        ExternalServicesSimulatorProperties properties = new ExternalServicesSimulatorProperties();

        // When
        properties.setFixedPaymentStatus(null);
        properties.setFixedSubscriptionStatus(null);

        // Then
        assertThat(properties.getFixedPaymentStatus()).isNull();
        assertThat(properties.getFixedSubscriptionStatus()).isNull();
    }
}
