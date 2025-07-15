package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductIdTest {

    @Test
    void shouldCreateProductIdWithValidValue() {
        // Given
        String validId = "PRODUCT-123";

        // When
        ProductId productId = ProductId.of(validId);

        // Then
        assertThat(productId.getValue()).isEqualTo(validId);
    }

    @Test
    void shouldTrimWhitespaceFromValue() {
        // Given
        String idWithWhitespace = "  PRODUCT-456  ";

        // When
        ProductId productId = ProductId.of(idWithWhitespace);

        // Then
        assertThat(productId.getValue()).isEqualTo("PRODUCT-456");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When & Then
        assertThatThrownBy(() -> ProductId.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product ID cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> ProductId.of(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product ID cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        // When & Then
        assertThatThrownBy(() -> ProductId.of("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product ID cannot be null or empty");
    }

    @Test
    void shouldGenerateRandomProductId() {
        // When
        ProductId productId1 = ProductId.generate();
        ProductId productId2 = ProductId.generate();

        // Then
        assertThat(productId1).isNotNull();
        assertThat(productId2).isNotNull();
        assertThat(productId1.getValue()).isNotEqualTo(productId2.getValue());
        assertThat(productId1).isNotEqualTo(productId2);
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        // Given
        ProductId productId1 = ProductId.of("PRODUCT-123");
        ProductId productId2 = ProductId.of("PRODUCT-123");

        // Then
        assertThat(productId1).isEqualTo(productId2);
        assertThat(productId1.hashCode()).isEqualTo(productId2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        // Given
        ProductId productId1 = ProductId.of("PRODUCT-123");
        ProductId productId2 = ProductId.of("PRODUCT-456");

        // Then
        assertThat(productId1).isNotEqualTo(productId2);
        assertThat(productId1.hashCode()).isNotEqualTo(productId2.hashCode());
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Given
        ProductId productId = ProductId.of("PRODUCT-123");

        // Then
        assertThat(productId).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        ProductId productId = ProductId.of("PRODUCT-123");
        String otherObject = "PRODUCT-123";

        // Then
        assertThat(productId).isNotEqualTo(otherObject);
    }

    @Test
    void shouldBeEqualToItself() {
        // Given
        ProductId productId = ProductId.of("PRODUCT-123");

        // Then
        assertThat(productId).isEqualTo(productId);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        // Given
        ProductId productId = ProductId.of("PRODUCT-123");

        // When
        int hashCode1 = productId.hashCode();
        int hashCode2 = productId.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        ProductId productId = ProductId.of("PRODUCT-123");

        // When
        String toString = productId.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).contains("PRODUCT-123");
    }

    @Test
    void shouldGenerateValidUUIDFormat() {
        // When
        ProductId productId = ProductId.generate();

        // Then
        assertThat(productId.getValue()).isNotNull();
        assertThat(productId.getValue()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}
