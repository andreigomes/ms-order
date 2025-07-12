package com.seguradora.msorder.infrastructure.adapter.out.external.dto;

import java.math.BigDecimal;

/**
 * DTO para solicitação de análise de risco de fraude
 */
public record FraudAnalysisRequest(
    String customerId,
    BigDecimal amount,
    String insuranceType,
    String description
) {}
