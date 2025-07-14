package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;

/**
 * Porta de saída para consulta à API de fraudes
 */
public interface FraudAnalysisPort {

    /**
     * Consulta a API de fraudes para analisar o risco do pedido
     * @param request Dados do pedido para análise
     * @return Nível de risco: REGULAR, HIGH_RISK, PREFERENTIAL, NO_INFO
     */
    String analyzeRisk(FraudAnalysisRequest request);
}
