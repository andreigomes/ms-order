package com.seguradora.msorder.integration.config;

import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
            public String analyzeRisk(FraudAnalysisRequest request) {
                // Clientes específicos para cada regra de teste
                String customerId = request.customerId();

                // Cliente REGULAR
                if ("CUST_REGULAR".equals(customerId)) {
                    return "REGULAR";
                }

                // Cliente PREFERENTIAL
                if ("CUST_PREFERENTIAL".equals(customerId) || "12345".equals(customerId)) {
                    return "PREFERENTIAL";
                }

                // Cliente NO_INFO
                if ("CUST_NO_INFO".equals(customerId)) {
                    return "NO_INFO";
                }

                // Default: HIGH_RISK para todos os outros clientes
                return "HIGH_RISK";
            }
        };
    }
}
