package com.seguradora.msorder.application.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentEventDataTest {

    @Test
    void shouldCreatePaymentEventDataWithAllFields() {
        // Given
        PaymentEventData eventData = new PaymentEventData();
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPaymentId("payment-123");
        paymentDetails.setAmount(new BigDecimal("1000.00"));

        // When
        eventData.setOrderId("order-456");
        eventData.setStatus("APPROVED");
        eventData.setReason("Payment processed successfully");
        eventData.setEventType("PAYMENT_PROCESSED");
        eventData.setTimestamp("2025-01-15T10:30:00Z");
        eventData.setPaymentDetails(paymentDetails);

        // Then
        assertThat(eventData.getOrderId()).isEqualTo("order-456");
        assertThat(eventData.getStatus()).isEqualTo("APPROVED");
        assertThat(eventData.getReason()).isEqualTo("Payment processed successfully");
        assertThat(eventData.getEventType()).isEqualTo("PAYMENT_PROCESSED");
        assertThat(eventData.getTimestamp()).isEqualTo("2025-01-15T10:30:00Z");
        assertThat(eventData.getPaymentDetails()).isEqualTo(paymentDetails);
    }

    @Test
    void shouldCreatePaymentEventDataWithNullValues() {
        // Given
        PaymentEventData eventData = new PaymentEventData();

        // When - não setamos nenhum valor

        // Then
        assertThat(eventData.getOrderId()).isNull();
        assertThat(eventData.getStatus()).isNull();
        assertThat(eventData.getReason()).isNull();
        assertThat(eventData.getEventType()).isNull();
        assertThat(eventData.getTimestamp()).isNull();
        assertThat(eventData.getPaymentDetails()).isNull();
    }

    @Test
    void shouldAllowSettingIndividualFields() {
        // Given
        PaymentEventData eventData = new PaymentEventData();

        // When
        eventData.setOrderId("order-123");
        eventData.setStatus("REJECTED");

        // Then
        assertThat(eventData.getOrderId()).isEqualTo("order-123");
        assertThat(eventData.getStatus()).isEqualTo("REJECTED");
        assertThat(eventData.getReason()).isNull();
        assertThat(eventData.getEventType()).isNull();
        assertThat(eventData.getTimestamp()).isNull();
        assertThat(eventData.getPaymentDetails()).isNull();
    }

    @Test
    void shouldAllowUpdatingFields() {
        // Given
        PaymentEventData eventData = new PaymentEventData();
        eventData.setStatus("PENDING");
        eventData.setReason("Processing payment");

        // When
        eventData.setStatus("APPROVED");
        eventData.setReason("Payment approved");

        // Then
        assertThat(eventData.getStatus()).isEqualTo("APPROVED");
        assertThat(eventData.getReason()).isEqualTo("Payment approved");
    }

    @Test
    void shouldHandlePaymentDetailsReference() {
        // Given
        PaymentEventData eventData = new PaymentEventData();
        PaymentDetails originalDetails = new PaymentDetails();
        originalDetails.setPaymentId("payment-001");
        originalDetails.setAmount(new BigDecimal("500.00"));

        PaymentDetails newDetails = new PaymentDetails();
        newDetails.setPaymentId("payment-002");
        newDetails.setAmount(new BigDecimal("750.00"));

        // When
        eventData.setPaymentDetails(originalDetails);
        assertThat(eventData.getPaymentDetails().getPaymentId()).isEqualTo("payment-001");

        eventData.setPaymentDetails(newDetails);

        // Then
        assertThat(eventData.getPaymentDetails().getPaymentId()).isEqualTo("payment-002");
        assertThat(eventData.getPaymentDetails().getAmount()).isEqualTo(new BigDecimal("750.00"));
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        PaymentEventData eventData = new PaymentEventData();

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
    void shouldHandleTypicalPaymentEventScenarios() {
        // Given
        PaymentEventData approvedEvent = new PaymentEventData();
        PaymentEventData rejectedEvent = new PaymentEventData();

        // When - Cenário de aprovação
        approvedEvent.setOrderId("order-001");
        approvedEvent.setStatus("APPROVED");
        approvedEvent.setEventType("PAYMENT_APPROVED");
        approvedEvent.setReason("Credit card payment successful");

        // Cenário de rejeição
        rejectedEvent.setOrderId("order-002");
        rejectedEvent.setStatus("REJECTED");
        rejectedEvent.setEventType("PAYMENT_REJECTED");
        rejectedEvent.setReason("Insufficient funds");

        // Then
        assertThat(approvedEvent.getStatus()).isEqualTo("APPROVED");
        assertThat(approvedEvent.getEventType()).isEqualTo("PAYMENT_APPROVED");
        assertThat(approvedEvent.getReason()).contains("successful");

        assertThat(rejectedEvent.getStatus()).isEqualTo("REJECTED");
        assertThat(rejectedEvent.getEventType()).isEqualTo("PAYMENT_REJECTED");
        assertThat(rejectedEvent.getReason()).contains("Insufficient");
    }
}
