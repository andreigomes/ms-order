package com.seguradora.msorder.application.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDetailsTest {

    @Test
    void shouldCreatePaymentDetailsWithAllFields() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();
        String paymentId = "payment-123";
        String paymentMethod = "CREDIT_CARD";
        BigDecimal amount = new BigDecimal("1500.50");
        String transactionId = "txn-456789";
        String processedAt = "2025-01-15T10:30:00Z";

        // When
        paymentDetails.setPaymentId(paymentId);
        paymentDetails.setPaymentMethod(paymentMethod);
        paymentDetails.setAmount(amount);
        paymentDetails.setTransactionId(transactionId);
        paymentDetails.setProcessedAt(processedAt);

        // Then
        assertThat(paymentDetails.getPaymentId()).isEqualTo(paymentId);
        assertThat(paymentDetails.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(paymentDetails.getAmount()).isEqualTo(amount);
        assertThat(paymentDetails.getTransactionId()).isEqualTo(transactionId);
        assertThat(paymentDetails.getProcessedAt()).isEqualTo(processedAt);
    }

    @Test
    void shouldCreatePaymentDetailsWithNullValues() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();

        // When - não setamos nenhum valor

        // Then
        assertThat(paymentDetails.getPaymentId()).isNull();
        assertThat(paymentDetails.getPaymentMethod()).isNull();
        assertThat(paymentDetails.getAmount()).isNull();
        assertThat(paymentDetails.getTransactionId()).isNull();
        assertThat(paymentDetails.getProcessedAt()).isNull();
    }

    @Test
    void shouldAllowSettingIndividualFields() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();

        // When
        paymentDetails.setPaymentId("payment-001");

        // Then
        assertThat(paymentDetails.getPaymentId()).isEqualTo("payment-001");
        assertThat(paymentDetails.getPaymentMethod()).isNull();
        assertThat(paymentDetails.getAmount()).isNull();
        assertThat(paymentDetails.getTransactionId()).isNull();
        assertThat(paymentDetails.getProcessedAt()).isNull();
    }

    @Test
    void shouldAllowUpdatingFields() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPaymentMethod("CREDIT_CARD");
        paymentDetails.setAmount(new BigDecimal("100.00"));

        // When
        paymentDetails.setPaymentMethod("PIX");
        paymentDetails.setAmount(new BigDecimal("200.00"));

        // Then
        assertThat(paymentDetails.getPaymentMethod()).isEqualTo("PIX");
        assertThat(paymentDetails.getAmount()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void shouldHandleZeroAmount() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // When
        paymentDetails.setAmount(zeroAmount);

        // Then
        assertThat(paymentDetails.getAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldHandleNegativeAmount() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();
        BigDecimal negativeAmount = new BigDecimal("-50.00");

        // When
        paymentDetails.setAmount(negativeAmount);

        // Then
        assertThat(paymentDetails.getAmount()).isEqualTo(negativeAmount);
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        PaymentDetails paymentDetails = new PaymentDetails();

        // When
        paymentDetails.setPaymentId("");
        paymentDetails.setPaymentMethod("");
        paymentDetails.setTransactionId("");
        paymentDetails.setProcessedAt("");

        // Then
        assertThat(paymentDetails.getPaymentId()).isEmpty();
        assertThat(paymentDetails.getPaymentMethod()).isEmpty();
        assertThat(paymentDetails.getTransactionId()).isEmpty();
        assertThat(paymentDetails.getProcessedAt()).isEmpty();
    }
}
