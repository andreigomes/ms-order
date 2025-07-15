package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerIdTest {

    @Test
    void shouldCreateCustomerIdWithValidValue() {
        // Given
        String validId = "customer-123";

        // When
        CustomerId customerId = new CustomerId(validId);

        // Then
        assertThat(customerId.getValue()).isEqualTo(validId);
    }

    @Test
    void shouldTrimWhitespaceFromValue() {
        // Given
        String idWithWhitespace = "  customer-456  ";

        // When
        CustomerId customerId = new CustomerId(idWithWhitespace);

        // Then
        assertThat(customerId.getValue()).isEqualTo("customer-456");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When & Then
        assertThatThrownBy(() -> new CustomerId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Customer ID cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> new CustomerId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Customer ID cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        // When & Then
        assertThatThrownBy(() -> new CustomerId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Customer ID cannot be null or empty");
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        // Given
        CustomerId customerId1 = new CustomerId("customer-123");
        CustomerId customerId2 = new CustomerId("customer-123");

        // Then
        assertThat(customerId1).isEqualTo(customerId2);
        assertThat(customerId1.hashCode()).isEqualTo(customerId2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        // Given
        CustomerId customerId1 = new CustomerId("customer-123");
        CustomerId customerId2 = new CustomerId("customer-456");

        // Then
        assertThat(customerId1).isNotEqualTo(customerId2);
        assertThat(customerId1.hashCode()).isNotEqualTo(customerId2.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Given
        CustomerId customerId = new CustomerId("customer-123");

        // Then
        assertThat(customerId).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        CustomerId customerId = new CustomerId("customer-123");
        String otherObject = "customer-123";

        // Then
        assertThat(customerId).isNotEqualTo(otherObject);
    }

    @Test
    void shouldBeEqualToItself() {
        // Given
        CustomerId customerId = new CustomerId("customer-123");

        // Then
        assertThat(customerId).isEqualTo(customerId);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        // Given
        CustomerId customerId = new CustomerId("customer-123");

        // When
        int hashCode1 = customerId.hashCode();
        int hashCode2 = customerId.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        CustomerId customerId = new CustomerId("customer-123");

        // When
        String toString = customerId.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).isNotEmpty();
    }
}
