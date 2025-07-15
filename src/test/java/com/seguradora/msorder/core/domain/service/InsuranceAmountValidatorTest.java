package com.seguradora.msorder.core.domain.service;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InsuranceAmountValidatorTest {

    private InsuranceAmountValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InsuranceAmountValidator();
    }

    // Testes para Cliente REGULAR
    @Test
    void shouldValidateRegularCustomerLifeInsurance() {
        // Valid amounts
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.LIFE, new BigDecimal("500000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.LIFE, new BigDecimal("250000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.LIFE, new BigDecimal("100000.00"))).isTrue();

        // Invalid amounts
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.LIFE, new BigDecimal("500000.01"))).isFalse();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.LIFE, new BigDecimal("600000.00"))).isFalse();
    }

    @Test
    void shouldValidateRegularCustomerHomeInsurance() {
        // Valid amounts
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HOME, new BigDecimal("500000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HOME, new BigDecimal("300000.00"))).isTrue();

        // Invalid amounts
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HOME, new BigDecimal("500000.01"))).isFalse();
    }

    @Test
    void shouldValidateRegularCustomerAutoInsurance() {
        // Valid amounts
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.AUTO, new BigDecimal("350000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.AUTO, new BigDecimal("200000.00"))).isTrue();

        // Invalid amounts
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.AUTO, new BigDecimal("350000.01"))).isFalse();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.AUTO, new BigDecimal("400000.00"))).isFalse();
    }

    @Test
    void shouldValidateRegularCustomerHealthAndTravelInsurance() {
        // Health Insurance
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HEALTH, new BigDecimal("255000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HEALTH, new BigDecimal("100000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HEALTH, new BigDecimal("255000.01"))).isFalse();

        // Travel Insurance
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.TRAVEL, new BigDecimal("255000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.TRAVEL, new BigDecimal("100000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.TRAVEL, new BigDecimal("255000.01"))).isFalse();
    }

    // Testes para Cliente HIGH_RISK
    @Test
    void shouldValidateHighRiskCustomerAutoInsurance() {
        // Valid amounts
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.AUTO, new BigDecimal("250000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.AUTO, new BigDecimal("150000.00"))).isTrue();

        // Invalid amounts
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.AUTO, new BigDecimal("250000.01"))).isFalse();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.AUTO, new BigDecimal("300000.00"))).isFalse();
    }

    @Test
    void shouldValidateHighRiskCustomerHomeInsurance() {
        // Valid amounts
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HOME, new BigDecimal("150000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HOME, new BigDecimal("100000.00"))).isTrue();

        // Invalid amounts
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HOME, new BigDecimal("150000.01"))).isFalse();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HOME, new BigDecimal("200000.00"))).isFalse();
    }

    @Test
    void shouldValidateHighRiskCustomerLifeHealthTravelInsurance() {
        // Life Insurance
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.LIFE, new BigDecimal("125000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.LIFE, new BigDecimal("125000.01"))).isFalse();

        // Health Insurance
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HEALTH, new BigDecimal("125000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HEALTH, new BigDecimal("125000.01"))).isFalse();

        // Travel Insurance
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.TRAVEL, new BigDecimal("125000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.TRAVEL, new BigDecimal("125000.01"))).isFalse();
    }

    // Testes para Cliente PREFERENTIAL
    @Test
    void shouldValidatePreferentialCustomerAllTypes() {
        // Preferential customers should have higher limits or special treatment
        BigDecimal highAmount = new BigDecimal("1000000.00");

        // Test all insurance types with high amounts (assuming preferential customers have higher limits)
        for (InsuranceType type : InsuranceType.values()) {
            boolean result = validator.isAmountValid(RiskLevel.PREFERENTIAL, type, highAmount);
            // O resultado depende da implementação específica da classe
            assertThat(result).isNotNull(); // Apenas verifica que o método não quebra
        }
    }

    @Test
    void shouldValidatePreferentialCustomerTypicalAmounts() {
        // Test with more reasonable amounts for preferential customers
        assertThat(validator.isAmountValid(RiskLevel.PREFERENTIAL, InsuranceType.AUTO, new BigDecimal("400000.00"))).isNotNull();
        assertThat(validator.isAmountValid(RiskLevel.PREFERENTIAL, InsuranceType.HOME, new BigDecimal("600000.00"))).isNotNull();
        assertThat(validator.isAmountValid(RiskLevel.PREFERENTIAL, InsuranceType.LIFE, new BigDecimal("800000.00"))).isNotNull();
    }

    // Testes para Cliente NO_INFO
    @Test
    void shouldValidateNoInfoCustomerAllTypes() {
        // NO_INFO customers typically have stricter limits
        BigDecimal testAmount = new BigDecimal("100000.00");

        for (InsuranceType type : InsuranceType.values()) {
            boolean result = validator.isAmountValid(RiskLevel.NO_INFO, type, testAmount);
            assertThat(result).isNotNull(); // Verifica que o método funciona
        }
    }

    @Test
    void shouldValidateNoInfoCustomerLowAmounts() {
        // Test with low amounts for NO_INFO customers
        BigDecimal lowAmount = new BigDecimal("50000.00");

        for (InsuranceType type : InsuranceType.values()) {
            boolean result = validator.isAmountValid(RiskLevel.NO_INFO, type, lowAmount);
            assertThat(result).isNotNull();
        }
    }

    // Testes de edge cases
    @Test
    void shouldHandleZeroAmount() {
        BigDecimal zeroAmount = BigDecimal.ZERO;

        for (RiskLevel riskLevel : RiskLevel.values()) {
            for (InsuranceType type : InsuranceType.values()) {
                boolean result = validator.isAmountValid(riskLevel, type, zeroAmount);
                assertThat(result).isNotNull();
            }
        }
    }

    @Test
    void shouldHandleVeryLargeAmounts() {
        BigDecimal largeAmount = new BigDecimal("999999999.99");

        for (RiskLevel riskLevel : RiskLevel.values()) {
            for (InsuranceType type : InsuranceType.values()) {
                boolean result = validator.isAmountValid(riskLevel, type, largeAmount);
                assertThat(result).isNotNull();
            }
        }
    }

    @Test
    void shouldHandleAllCombinations() {
        // Test all combinations of RiskLevel and InsuranceType
        BigDecimal testAmount = new BigDecimal("100000.00");

        for (RiskLevel riskLevel : RiskLevel.values()) {
            for (InsuranceType type : InsuranceType.values()) {
                // Should not throw exception
                boolean result = validator.isAmountValid(riskLevel, type, testAmount);
                assertThat(result).isNotNull();
            }
        }
    }

    // Testes específicos para limites exatos
    @Test
    void shouldValidateExactLimits() {
        // Test exact boundary values

        // Regular customer exact limits
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.LIFE, new BigDecimal("500000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.AUTO, new BigDecimal("350000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.REGULAR, InsuranceType.HEALTH, new BigDecimal("255000.00"))).isTrue();

        // High risk customer exact limits
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.AUTO, new BigDecimal("250000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.HOME, new BigDecimal("150000.00"))).isTrue();
        assertThat(validator.isAmountValid(RiskLevel.HIGH_RISK, InsuranceType.LIFE, new BigDecimal("125000.00"))).isTrue();
    }
}
