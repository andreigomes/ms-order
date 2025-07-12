package com.seguradora.msorder.infrastructure.adapter.out.external.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta da análise de risco de fraude
 */
public record FraudAnalysisResponse(
    String customerId,
    String riskLevel, // LOW, MEDIUM, HIGH, BLOCKED
    String reason,
    double riskScore,
    LocalDateTime analysisDate,
    String analysisId
) {}
