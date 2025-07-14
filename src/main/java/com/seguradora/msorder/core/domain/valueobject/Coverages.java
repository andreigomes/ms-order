package com.seguradora.msorder.core.domain.valueobject;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Value Object para representar as coberturas de uma apólice
 */
public class Coverages {
    private final Map<String, BigDecimal> coverageMap;

    private Coverages(Map<String, BigDecimal> coverageMap) {
        this.coverageMap = Collections.unmodifiableMap(Objects.requireNonNull(coverageMap, "Coverage map cannot be null"));
    }

    public static Coverages of(Map<String, BigDecimal> coverageMap) {
        if (coverageMap == null || coverageMap.isEmpty()) {
            throw new IllegalArgumentException("Coverage map cannot be null or empty");
        }

        // Validar que todos os valores são positivos
        coverageMap.values().forEach(value -> {
            if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("All coverage values must be positive");
            }
        });

        return new Coverages(coverageMap);
    }

    public Map<String, BigDecimal> getCoverageMap() {
        return coverageMap;
    }

    public BigDecimal getTotalCoverageAmount() {
        return coverageMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coverages coverages = (Coverages) o;
        return Objects.equals(coverageMap, coverages.coverageMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverageMap);
    }

    @Override
    public String toString() {
        return "Coverages{" + coverageMap + '}';
    }
}
