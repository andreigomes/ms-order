package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskLevelTest {

    @Test
    void shouldHaveAllRequiredRiskLevels() {
        // When & Then
        assertThat(RiskLevel.REGULAR).isNotNull();
        assertThat(RiskLevel.HIGH_RISK).isNotNull();
        assertThat(RiskLevel.PREFERENTIAL).isNotNull();
        assertThat(RiskLevel.NO_INFO).isNotNull();
    }

    @Test
    void shouldHaveCorrectDescriptions() {
        // When & Then
        assertThat(RiskLevel.REGULAR.getDescription()).isEqualTo("Cliente com perfil de risco baixo");
        assertThat(RiskLevel.HIGH_RISK.getDescription()).isEqualTo("Cliente com perfil de risco alto");
        assertThat(RiskLevel.PREFERENTIAL.getDescription()).isEqualTo("Cliente preferencial com bom relacionamento");
        assertThat(RiskLevel.NO_INFO.getDescription()).isEqualTo("Cliente sem informações suficientes");
    }

    @Test
    void shouldCreateFromStringRegular() {
        // When & Then
        assertThat(RiskLevel.fromString("REGULAR")).isEqualTo(RiskLevel.REGULAR);
        assertThat(RiskLevel.fromString("regular")).isEqualTo(RiskLevel.REGULAR);
        assertThat(RiskLevel.fromString("Regular")).isEqualTo(RiskLevel.REGULAR);
        assertThat(RiskLevel.fromString("  REGULAR  ")).isEqualTo(RiskLevel.REGULAR);
    }

    @Test
    void shouldCreateFromStringHighRisk() {
        // When & Then
        assertThat(RiskLevel.fromString("HIGH_RISK")).isEqualTo(RiskLevel.HIGH_RISK);
        assertThat(RiskLevel.fromString("high_risk")).isEqualTo(RiskLevel.HIGH_RISK);
        assertThat(RiskLevel.fromString("High_Risk")).isEqualTo(RiskLevel.HIGH_RISK);
        assertThat(RiskLevel.fromString("  HIGH_RISK  ")).isEqualTo(RiskLevel.HIGH_RISK);
    }

    @Test
    void shouldCreateFromStringPreferential() {
        // When & Then
        assertThat(RiskLevel.fromString("PREFERENTIAL")).isEqualTo(RiskLevel.PREFERENTIAL);
        assertThat(RiskLevel.fromString("preferential")).isEqualTo(RiskLevel.PREFERENTIAL);
        assertThat(RiskLevel.fromString("Preferential")).isEqualTo(RiskLevel.PREFERENTIAL);
        assertThat(RiskLevel.fromString("  PREFERENTIAL  ")).isEqualTo(RiskLevel.PREFERENTIAL);
    }

    @Test
    void shouldReturnNoInfoWhenStringIsNull() {
        // When & Then
        assertThat(RiskLevel.fromString(null)).isEqualTo(RiskLevel.NO_INFO);
    }

    @Test
    void shouldReturnNoInfoWhenStringIsEmpty() {
        // When & Then
        assertThat(RiskLevel.fromString("")).isEqualTo(RiskLevel.NO_INFO);
        assertThat(RiskLevel.fromString("   ")).isEqualTo(RiskLevel.NO_INFO);
    }

    @Test
    void shouldReturnNoInfoForUnknownValues() {
        // When & Then
        assertThat(RiskLevel.fromString("UNKNOWN")).isEqualTo(RiskLevel.NO_INFO);
        assertThat(RiskLevel.fromString("INVALID")).isEqualTo(RiskLevel.NO_INFO);
        assertThat(RiskLevel.fromString("123")).isEqualTo(RiskLevel.NO_INFO);
    }

    @Test
    void shouldHaveAllValuesMethod() {
        // When
        RiskLevel[] values = RiskLevel.values();

        // Then
        assertThat(values).hasSize(4);
        assertThat(values).containsExactlyInAnyOrder(
            RiskLevel.REGULAR,
            RiskLevel.HIGH_RISK,
            RiskLevel.PREFERENTIAL,
            RiskLevel.NO_INFO
        );
    }

    @Test
    void shouldSupportValueOfOperation() {
        // When & Then
        assertThat(RiskLevel.valueOf("REGULAR")).isEqualTo(RiskLevel.REGULAR);
        assertThat(RiskLevel.valueOf("HIGH_RISK")).isEqualTo(RiskLevel.HIGH_RISK);
        assertThat(RiskLevel.valueOf("PREFERENTIAL")).isEqualTo(RiskLevel.PREFERENTIAL);
        assertThat(RiskLevel.valueOf("NO_INFO")).isEqualTo(RiskLevel.NO_INFO);
    }

    @Test
    void shouldBeComparable() {
        // When & Then
        assertThat(RiskLevel.REGULAR).isEqualTo(RiskLevel.REGULAR);
        assertThat(RiskLevel.HIGH_RISK).isNotEqualTo(RiskLevel.REGULAR);
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // When & Then
        assertThat(RiskLevel.REGULAR.name()).isEqualTo("REGULAR");
        assertThat(RiskLevel.HIGH_RISK.name()).isEqualTo("HIGH_RISK");
        assertThat(RiskLevel.PREFERENTIAL.name()).isEqualTo("PREFERENTIAL");
        assertThat(RiskLevel.NO_INFO.name()).isEqualTo("NO_INFO");
    }
}
