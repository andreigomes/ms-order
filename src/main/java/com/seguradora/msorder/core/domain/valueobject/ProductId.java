package com.seguradora.msorder.core.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object para representar o ID do produto
 */
public class ProductId {
    private final String value;

    private ProductId(String value) {
        this.value = Objects.requireNonNull(value, "Product ID cannot be null");
    }

    public static ProductId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        return new ProductId(value.trim());
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
