package com.seguradora.msorder.infrastructure.adapter.out.external;

import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FraudAnalysisAdapter implements FraudAnalysisPort {

    private final RestTemplate restTemplate;
    private final String fraudApiBaseUrl;

    public FraudAnalysisAdapter(
            @Value("${fraud-api.base-url:http://localhost:8081}") String fraudApiBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.fraudApiBaseUrl = fraudApiBaseUrl;
    }

    @Override
    public String analyzeRisk(FraudAnalysisRequest request) {
        try {
            String url = fraudApiBaseUrl + "/api/v1/fraud/analyze";
            FraudAnalysisResponse response = restTemplate.postForObject(
                    url, request, FraudAnalysisResponse.class);

            if (response != null) {
                return response.getClassification() != null ?
                        response.getClassification() :
                        response.getRiskLevel();
            }
            return "REGULAR";
        } catch (Exception e) {
            return "REGULAR";
        }
    }
}