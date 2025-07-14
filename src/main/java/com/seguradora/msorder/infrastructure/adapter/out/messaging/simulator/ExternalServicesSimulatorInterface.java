package com.seguradora.msorder.infrastructure.adapter.out.messaging.simulator;

import com.seguradora.msorder.core.domain.entity.Order;

import java.math.BigDecimal;

/**
 * Interface para simulação de serviços externos
 */
public interface ExternalServicesSimulatorInterface {

    void simulatePaymentProcessing(String orderId, String customerId, BigDecimal amount);

    void simulateSubscriptionAnalysis(String orderId, String customerId, String insuranceType, BigDecimal amount);

    void simulatePaymentAndSubscriptionProcessing(Order order);

    void triggerExternalServices(Order order);
}
