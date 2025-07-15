package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoveragesTest {

    @Test
    void shouldCreateCoveragesWithValidMap() {
        // Given
        Map<String, BigDecimal> coverageMap = Map.of(
            "collision", new BigDecimal("50000.00"),
            "theft", new BigDecimal("30000.00")
        );

        // When
        Coverages coverages = Coverages.of(coverageMap);

        // Then
        assertThat(coverages.getCoverageMap()).isEqualTo(coverageMap);
        assertThat(coverages.getCoverageMap()).hasSize(2);
    }

    @Test
    void shouldCalculateTotalCoverageAmount() {
        // Given
        Map<String, BigDecimal> coverageMap = Map.of(
            "collision", new BigDecimal("50000.00"),
            "theft", new BigDecimal("30000.00"),
            "fire", new BigDecimal("20000.00")
        );

        // When
        Coverages coverages = Coverages.of(coverageMap);
        BigDecimal total = coverages.getTotalCoverageAmount();

        // Then
        assertThat(total).isEqualTo(new BigDecimal("100000.00"));
    }

    @Test
    void shouldThrowExceptionWhenMapIsNull() {
        // When & Then
        assertThatThrownBy(() -> Coverages.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Coverage map cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenMapIsEmpty() {
        // Given
        Map<String, BigDecimal> emptyMap = Map.of();

        // When & Then
        assertThatThrownBy(() -> Coverages.of(emptyMap))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Coverage map cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // Given
        Map<String, BigDecimal> mapWithNullValue = new HashMap<>();
        mapWithNullValue.put("collision", null);

        // When & Then
        assertThatThrownBy(() -> Coverages.of(mapWithNullValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("All coverage values must be positive");
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        // Given
        Map<String, BigDecimal> mapWithZeroValue = Map.of(
            "collision", BigDecimal.ZERO
        );

        // When & Then
        assertThatThrownBy(() -> Coverages.of(mapWithZeroValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("All coverage values must be positive");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        // Given
        Map<String, BigDecimal> mapWithNegativeValue = Map.of(
            "collision", new BigDecimal("-1000.00")
        );

        // When & Then
        assertThatThrownBy(() -> Coverages.of(mapWithNegativeValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("All coverage values must be positive");
    }

    @Test
    void shouldReturnImmutableMap() {
        // Given
        Map<String, BigDecimal> originalMap = new HashMap<>();
        originalMap.put("collision", new BigDecimal("50000.00"));
        Coverages coverages = Coverages.of(originalMap);

        // When & Then
        assertThatThrownBy(() -> coverages.getCoverageMap().put("theft", new BigDecimal("30000.00")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldBeEqualWhenSameMap() {
        // Given
        Map<String, BigDecimal> coverageMap = Map.of(
            "collision", new BigDecimal("50000.00"),
            "theft", new BigDecimal("30000.00")
        );
        Coverages coverages1 = Coverages.of(coverageMap);
        Coverages coverages2 = Coverages.of(coverageMap);

        // Then
        assertThat(coverages1).isEqualTo(coverages2);
        assertThat(coverages1.hashCode()).isEqualTo(coverages2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentMap() {
        // Given
        Map<String, BigDecimal> coverageMap1 = Map.of("collision", new BigDecimal("50000.00"));
        Map<String, BigDecimal> coverageMap2 = Map.of("theft", new BigDecimal("30000.00"));
        Coverages coverages1 = Coverages.of(coverageMap1);
        Coverages coverages2 = Coverages.of(coverageMap2);

        // Then
        assertThat(coverages1).isNotEqualTo(coverages2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Given
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("50000.00")));

        // Then
        assertThat(coverages).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("50000.00")));
        String otherObject = "test";

        // Then
        assertThat(coverages).isNotEqualTo(otherObject);
    }

    @Test
    void shouldBeEqualToItself() {
        // Given
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("50000.00")));

        // Then
        assertThat(coverages).isEqualTo(coverages);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        // Given
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("50000.00")));

        // When
        int hashCode1 = coverages.hashCode();
        int hashCode2 = coverages.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void shouldCalculateTotalForSingleCoverage() {
        // Given
        Map<String, BigDecimal> coverageMap = Map.of("collision", new BigDecimal("75000.50"));

        // When
        Coverages coverages = Coverages.of(coverageMap);
        BigDecimal total = coverages.getTotalCoverageAmount();

        // Then
        assertThat(total).isEqualTo(new BigDecimal("75000.50"));
    }

    @Test
    void shouldHandleDecimalPrecision() {
        // Given
        Map<String, BigDecimal> coverageMap = Map.of(
            "collision", new BigDecimal("50000.123"),
            "theft", new BigDecimal("30000.456")
        );

        // When
        Coverages coverages = Coverages.of(coverageMap);
        BigDecimal total = coverages.getTotalCoverageAmount();

        // Then
        assertThat(total).isEqualTo(new BigDecimal("80000.579"));
    }
}
