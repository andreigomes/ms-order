package com.seguradora.msorder.infrastructure.adapter.out.external.dto;

/**
 * DTO para resposta de análise de fraude
 */
public class FraudAnalysisResponse {
    private String riskLevel;
    private String reason;

    public FraudAnalysisResponse() {}

    public FraudAnalysisResponse(String riskLevel, String reason) {
        this.riskLevel = riskLevel;
        this.reason = reason;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
