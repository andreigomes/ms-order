package com.seguradora.msorder.infrastructure.adapter.out.external;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Adapter para consumo da API de fraudes
 */
@Component
public class FraudAnalysisAdapter implements FraudAnalysisPort {

    private static final Logger logger = LoggerFactory.getLogger(FraudAnalysisAdapter.class);

    private final RestTemplate restTemplate;
    private final String fraudApiUrl;

    public FraudAnalysisAdapter(RestTemplate restTemplate,
                               @Value("${app.fraud-api.url}") String fraudApiUrl) {
        this.restTemplate = restTemplate;
        this.fraudApiUrl = fraudApiUrl;
    }

    @Override
    public String analyzeRisk(Order order) {
        try {
            logger.info("Consultando API de fraudes para customer: {}", order.getCustomerId().getValue());

            FraudAnalysisRequest request = new FraudAnalysisRequest(
                order.getCustomerId().getValue(),
                order.getAmount(),
                order.getInsuranceType().name(),
                order.getDescription()
            );

            FraudAnalysisResponse response = restTemplate.postForObject(
                fraudApiUrl + "/fraud-analysis",
                request,
                FraudAnalysisResponse.class
            );

            if (response != null) {
                logger.info("Análise de fraude concluída - Customer: {}, Risk: {}, Score: {}",
                           response.customerId(), response.riskLevel(), response.riskScore());
                return response.riskLevel();
            }

            logger.warn("Resposta nula da API de fraudes para customer: {}", order.getCustomerId().getValue());
            return "MEDIUM"; // Fallback para risco médio

        } catch (Exception e) {
            logger.error("Erro ao consultar API de fraudes para customer: {}",
                        order.getCustomerId().getValue(), e);
            return "HIGH"; // Fallback para alto risco em caso de erro
        }
    }

    @Override
    public boolean isCustomerBlocked(String customerId) {
        try {
            logger.info("Verificando se customer está bloqueado: {}", customerId);

            Boolean isBlocked = restTemplate.getForObject(
                fraudApiUrl + "/customers/{customerId}/blocked",
                Boolean.class,
                customerId
            );

            return Boolean.TRUE.equals(isBlocked);

        } catch (Exception e) {
            logger.error("Erro ao verificar bloqueio do customer: {}", customerId, e);
            return false; // Fallback para não bloqueado em caso de erro
        }
    }
}
