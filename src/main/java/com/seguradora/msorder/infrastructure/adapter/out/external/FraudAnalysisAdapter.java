package com.seguradora.msorder.infrastructure.adapter.out.external;

import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Adaptador para consumir a API de análise de fraudes
 */
@Component
public class FraudAnalysisAdapter implements FraudAnalysisPort {

    private final WebClient webClient;
    private final String fraudApiBaseUrl;

    public FraudAnalysisAdapter(WebClient.Builder webClientBuilder,
                               @Value("${fraud-api.base-url:http://localhost:8081}") String fraudApiBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(fraudApiBaseUrl).build();
        this.fraudApiBaseUrl = fraudApiBaseUrl;
    }

    @Override
    public String analyzeRisk(FraudAnalysisRequest request) {
        try {
            FraudAnalysisResponse response = webClient.post()
                .uri("/api/v1/fraud/analyze")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FraudAnalysisResponse.class)
                .block();

            // Usar classification primeiro, depois riskLevel para compatibilidade
            if (response != null) {
                return response.getClassification() != null ?
                       response.getClassification() :
                       response.getRiskLevel();
            }
            return "REGULAR";
        } catch (Exception e) {
            // Em caso de erro, assumimos risco regular
            return "REGULAR";
        }
    }
}
