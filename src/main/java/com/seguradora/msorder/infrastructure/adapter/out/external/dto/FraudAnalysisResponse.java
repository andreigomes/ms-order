package com.seguradora.msorder.infrastructure.adapter.out.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de análise de fraude
 */
public class FraudAnalysisResponse {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("analyzedAt")
    private LocalDateTime analyzedAt;

    @JsonProperty("classification")
    private String classification; // HIGH_RISK, REGULAR, PREFERENTIAL, NO_INFO

    @JsonProperty("occurrences")
    private List<FraudOccurrence> occurrences;

    // Campos de compatibilidade com a estrutura anterior
    private String riskLevel;
    private String reason;

    public FraudAnalysisResponse() {}

    public FraudAnalysisResponse(String orderId, String customerId, LocalDateTime analyzedAt,
                                String classification, List<FraudOccurrence> occurrences) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.analyzedAt = analyzedAt;
        this.classification = classification;
        this.occurrences = occurrences;
        // Manter compatibilidade
        this.riskLevel = classification;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
        this.riskLevel = classification; // Compatibilidade
    }

    public List<FraudOccurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<FraudOccurrence> occurrences) {
        this.occurrences = occurrences;
    }

    // Métodos de compatibilidade
    public String getRiskLevel() {
        return riskLevel != null ? riskLevel : classification;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        this.classification = riskLevel; // Sincronizar
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
