package com.seguradora.msorder.integration.config;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

/**
 * Configuração de teste para mock da API de fraudes
 */
@TestConfiguration
public class TestFraudAnalysisConfig {

    @Bean
    @Primary
    public FraudAnalysisPort testFraudAnalysisPort() {
        return new FraudAnalysisPort() {
            @Override
            public String analyzeRisk(Order order) {
                // Simula lógica de análise de risco baseada no valor
                BigDecimal amount = order.getAmount();

                if (amount.compareTo(new BigDecimal("1000")) < 0) {
                    return "LOW";
                } else if (amount.compareTo(new BigDecimal("5000")) < 0) {
                    return "MEDIUM";
                } else if (amount.compareTo(new BigDecimal("20000")) < 0) {
                    return "HIGH";
                } else {
                    return "BLOCKED";
                }
            }

            @Override
            public boolean isCustomerBlocked(String customerId) {
                // Simula clientes bloqueados
                return customerId.startsWith("BLOCKED_") ||
                       customerId.equals("999999999");
            }
        };
    }
}
