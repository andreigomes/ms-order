package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderIdTest {

    @Test
    void shouldCreateOrderIdWithValidUUID() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        OrderId orderId = new OrderId(uuid);

        // Then
        assertThat(orderId.getValue()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenUUIDIsNull() {
        // When & Then
        assertThatThrownBy(() -> new OrderId(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Order ID cannot be null");
    }

    @Test
    void shouldGenerateRandomOrderId() {
        // When
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();

        // Then
        assertThat(orderId1).isNotNull();
        assertThat(orderId2).isNotNull();
        assertThat(orderId1.getValue()).isNotEqualTo(orderId2.getValue());
        assertThat(orderId1).isNotEqualTo(orderId2);
    }

    @Test
    void shouldCreateOrderIdFromValidString() {
        // Given
        String validUuid = "550e8400-e29b-41d4-a716-446655440000";

        // When
        OrderId orderId = OrderId.of(validUuid);

        // Then
        assertThat(orderId.getValue()).isEqualTo(UUID.fromString(validUuid));
    }

    @Test
    void shouldThrowExceptionWhenStringIsInvalidUUID() {
        // Given
        String invalidUuid = "invalid-uuid";

        // When & Then
        assertThatThrownBy(() -> OrderId.of(invalidUuid))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenStringIsNull() {
        // When & Then
        assertThatThrownBy(() -> OrderId.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Cannot invoke \"String.length()\" because \"name\" is null");
    }

    @Test
    void shouldBeEqualWhenSameUUID() {
        // Given
        UUID uuid = UUID.randomUUID();
        OrderId orderId1 = new OrderId(uuid);
        OrderId orderId2 = new OrderId(uuid);

        // Then
        assertThat(orderId1).isEqualTo(orderId2);
        assertThat(orderId1.hashCode()).isEqualTo(orderId2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentUUID() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();

        // Then
        assertThat(orderId1).isNotEqualTo(orderId2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Given
        OrderId orderId = OrderId.generate();

        // Then
        assertThat(orderId).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        OrderId orderId = OrderId.generate();
        String otherObject = "test";

        // Then
        assertThat(orderId).isNotEqualTo(otherObject);
    }

    @Test
    void shouldBeEqualToItself() {
        // Given
        OrderId orderId = OrderId.generate();

        // Then
        assertThat(orderId).isEqualTo(orderId);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        int hashCode1 = orderId.hashCode();
        int hashCode2 = orderId.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        String toString = orderId.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).contains(orderId.getValue().toString());
    }

    @Test
    void shouldCreateFromStringAndBackToString() {
        // Given
        String originalUuid = "550e8400-e29b-41d4-a716-446655440000";

        // When
        OrderId orderId = OrderId.of(originalUuid);
        String resultUuid = orderId.getValue().toString();

        // Then
        assertThat(resultUuid).isEqualTo(originalUuid);
    }
}
