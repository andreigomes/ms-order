package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void shouldHaveAllRequiredStatuses() {
        // When & Then
        assertThat(OrderStatus.RECEIVED).isNotNull();
        assertThat(OrderStatus.VALIDATED).isNotNull();
        assertThat(OrderStatus.PENDING).isNotNull();
        assertThat(OrderStatus.APPROVED).isNotNull();
        assertThat(OrderStatus.REJECTED).isNotNull();
        assertThat(OrderStatus.CANCELLED).isNotNull();
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // When & Then
        assertThat(OrderStatus.RECEIVED.name()).isEqualTo("RECEIVED");
        assertThat(OrderStatus.VALIDATED.name()).isEqualTo("VALIDATED");
        assertThat(OrderStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(OrderStatus.APPROVED.name()).isEqualTo("APPROVED");
        assertThat(OrderStatus.REJECTED.name()).isEqualTo("REJECTED");
        assertThat(OrderStatus.CANCELLED.name()).isEqualTo("CANCELLED");
    }

    @Test
    void shouldBeComparable() {
        // When & Then
        assertThat(OrderStatus.RECEIVED).isEqualTo(OrderStatus.RECEIVED);
        assertThat(OrderStatus.VALIDATED).isNotEqualTo(OrderStatus.RECEIVED);
    }

    @Test
    void shouldSupportValueOfOperation() {
        // When & Then
        assertThat(OrderStatus.valueOf("RECEIVED")).isEqualTo(OrderStatus.RECEIVED);
        assertThat(OrderStatus.valueOf("VALIDATED")).isEqualTo(OrderStatus.VALIDATED);
        assertThat(OrderStatus.valueOf("PENDING")).isEqualTo(OrderStatus.PENDING);
        assertThat(OrderStatus.valueOf("APPROVED")).isEqualTo(OrderStatus.APPROVED);
        assertThat(OrderStatus.valueOf("REJECTED")).isEqualTo(OrderStatus.REJECTED);
        assertThat(OrderStatus.valueOf("CANCELLED")).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldHaveAllValuesMethod() {
        // When
        OrderStatus[] values = OrderStatus.values();

        // Then
        assertThat(values).hasSize(6);
        assertThat(values).contains(
            OrderStatus.RECEIVED,
            OrderStatus.VALIDATED,
            OrderStatus.PENDING,
            OrderStatus.APPROVED,
            OrderStatus.REJECTED,
            OrderStatus.CANCELLED
        );
    }
}
