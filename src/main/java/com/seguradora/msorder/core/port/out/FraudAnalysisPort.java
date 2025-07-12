package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.core.domain.entity.Order;

/**
 * Porta de saída para consulta à API de fraudes
 */
public interface FraudAnalysisPort {

    /**
     * Consulta a API de fraudes para analisar o risco do pedido
     * @param order Pedido a ser analisado
     * @return Nível de risco: LOW, MEDIUM, HIGH, BLOCKED
     */
    String analyzeRisk(Order order);

    /**
     * Verifica se o cliente está na lista de bloqueados
     * @param customerId ID do cliente
     * @return true se estiver bloqueado
     */
    boolean isCustomerBlocked(String customerId);
}
