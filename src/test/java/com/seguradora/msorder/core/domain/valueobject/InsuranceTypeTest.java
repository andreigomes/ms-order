package com.seguradora.msorder.core.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsuranceTypeTest {

    @Test
    void shouldHaveAllRequiredTypes() {
        // When & Then
        assertThat(InsuranceType.AUTO).isNotNull();
        assertThat(InsuranceType.HOME).isNotNull();
        assertThat(InsuranceType.LIFE).isNotNull();
        assertThat(InsuranceType.TRAVEL).isNotNull();
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // When & Then
        assertThat(InsuranceType.AUTO.name()).isEqualTo("AUTO");
        assertThat(InsuranceType.HOME.name()).isEqualTo("HOME");
        assertThat(InsuranceType.LIFE.name()).isEqualTo("LIFE");
        assertThat(InsuranceType.TRAVEL.name()).isEqualTo("TRAVEL");
    }

    @Test
    void shouldBeComparable() {
        // When & Then
        assertThat(InsuranceType.AUTO).isEqualTo(InsuranceType.AUTO);
        assertThat(InsuranceType.HOME).isNotEqualTo(InsuranceType.AUTO);
    }

    @Test
    void shouldSupportValueOfOperation() {
        // When & Then
        assertThat(InsuranceType.valueOf("AUTO")).isEqualTo(InsuranceType.AUTO);
        assertThat(InsuranceType.valueOf("HOME")).isEqualTo(InsuranceType.HOME);
        assertThat(InsuranceType.valueOf("LIFE")).isEqualTo(InsuranceType.LIFE);
        assertThat(InsuranceType.valueOf("TRAVEL")).isEqualTo(InsuranceType.TRAVEL);
    }

    @Test
    void shouldHaveAllValuesMethod() {
        // When
        InsuranceType[] values = InsuranceType.values();

        // Then
        assertThat(values).hasSize(6); // AUTO, HOME, LIFE, HEALTH, TRAVEL, BUSINESS
        assertThat(values).containsExactlyInAnyOrder(
            InsuranceType.AUTO,
            InsuranceType.HOME,
            InsuranceType.LIFE,
            InsuranceType.HEALTH,
            InsuranceType.TRAVEL,
            InsuranceType.BUSINESS
        );
    }
}
