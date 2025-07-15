package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodTest {

    @Test
    void shouldHaveAllRequiredMethods() {
        // When & Then
        assertThat(PaymentMethod.CREDIT_CARD).isNotNull();
        assertThat(PaymentMethod.PIX).isNotNull();
        assertThat(PaymentMethod.BANK_TRANSFER).isNotNull();
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // When & Then
        assertThat(PaymentMethod.CREDIT_CARD.name()).isEqualTo("CREDIT_CARD");
        assertThat(PaymentMethod.PIX.name()).isEqualTo("PIX");
        assertThat(PaymentMethod.BANK_TRANSFER.name()).isEqualTo("BANK_TRANSFER");
    }

    @Test
    void shouldBeComparable() {
        // When & Then
        assertThat(PaymentMethod.CREDIT_CARD).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(PaymentMethod.PIX).isNotEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void shouldSupportValueOfOperation() {
        // When & Then
        assertThat(PaymentMethod.valueOf("CREDIT_CARD")).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(PaymentMethod.valueOf("PIX")).isEqualTo(PaymentMethod.PIX);
        assertThat(PaymentMethod.valueOf("BANK_TRANSFER")).isEqualTo(PaymentMethod.BANK_TRANSFER);
    }

    @Test
    void shouldHaveAllValuesMethod() {
        // When
        PaymentMethod[] values = PaymentMethod.values();

        // Then
        assertThat(values).hasSize(5);
        assertThat(values).contains(
            PaymentMethod.CREDIT_CARD,
            PaymentMethod.DEBIT_ACCOUNT,
            PaymentMethod.BOLETO,
            PaymentMethod.PIX,
            PaymentMethod.BANK_TRANSFER
        );
    }
}
