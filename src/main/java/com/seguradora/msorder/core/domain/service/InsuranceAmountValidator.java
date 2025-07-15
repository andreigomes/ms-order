package com.seguradora.msorder.core.domain.service;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.RiskLevel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Validador de valores de seguro por tipo de cliente conforme regras de negócio
 */
@Service
public class InsuranceAmountValidator {

    /**
     * Valida se o valor do seguro está dentro dos limites para o tipo de cliente
     */
    public boolean isAmountValid(RiskLevel riskLevel, InsuranceType insuranceType, BigDecimal amount) {
        return switch (riskLevel) {
            case REGULAR -> validateRegularCustomer(insuranceType, amount);
            case HIGH_RISK -> validateHighRiskCustomer(insuranceType, amount);
            case PREFERENTIAL -> validatePreferentialCustomer(insuranceType, amount);
            case NO_INFO -> validateNoInfoCustomer(insuranceType, amount);
        };
    }

    /**
     * Cliente Regular - Perfil de risco baixo
     */
    private boolean validateRegularCustomer(InsuranceType type, BigDecimal amount) {
        return switch (type) {
            case LIFE, HOME -> amount.compareTo(new BigDecimal("500000.00")) <= 0;
            case AUTO -> amount.compareTo(new BigDecimal("350000.00")) <= 0;
            case HEALTH, TRAVEL -> amount.compareTo(new BigDecimal("255000.00")) <= 0;
            default -> false; // Para tipos não suportados
        };
    }

    /**
     * Cliente Alto Risco - Perfil de maior risco
     */
    private boolean validateHighRiskCustomer(InsuranceType type, BigDecimal amount) {
        return switch (type) {
            case AUTO -> amount.compareTo(new BigDecimal("250000.00")) <= 0;
            case HOME -> amount.compareTo(new BigDecimal("150000.00")) <= 0;
            case LIFE, HEALTH, TRAVEL -> amount.compareTo(new BigDecimal("125000.00")) <= 0;
            default -> false; // Para tipos não suportados
        };
    }

    /**
     * Cliente Preferencial - Bom relacionamento com a seguradora
     */
    private boolean validatePreferentialCustomer(InsuranceType type, BigDecimal amount) {
        return switch (type) {
            case LIFE -> amount.compareTo(new BigDecimal("800000.00")) <= 0;
            case AUTO, HOME -> amount.compareTo(new BigDecimal("450000.00")) <= 0;
            case HEALTH, TRAVEL -> amount.compareTo(new BigDecimal("375000.00")) <= 0;
            default -> false; // Para tipos não suportados
        };
    }

    /**
     * Cliente Sem Informação - Pouco histórico com a seguradora
     */
    private boolean validateNoInfoCustomer(InsuranceType type, BigDecimal amount) {
        return switch (type) {
            case LIFE, HOME -> amount.compareTo(new BigDecimal("200000.00")) <= 0;
            case AUTO -> amount.compareTo(new BigDecimal("75000.00")) <= 0;
            case HEALTH, TRAVEL -> amount.compareTo(new BigDecimal("55000.00")) <= 0;
            default -> false; // Para tipos não suportados
        };
    }
}
