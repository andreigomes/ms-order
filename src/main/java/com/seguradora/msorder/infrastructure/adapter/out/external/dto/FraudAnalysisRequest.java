package com.seguradora.msorder.infrastructure.adapter.out.external.dto;

import java.math.BigDecimal;

/**
 * DTO para requisição de análise de fraude
 */
public record FraudAnalysisRequest(
    String orderId,
    String customerId,
    BigDecimal amount,
    String insuranceType,
    String description
) {}
