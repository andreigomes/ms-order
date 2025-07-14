package com.seguradora.msorder.infrastructure.adapter.out.messaging.simulator;

import com.seguradora.msorder.core.domain.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Implementação vazia do simulador quando está desabilitado
 */
@Component
@ConditionalOnProperty(prefix = "app.simulator", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledExternalServicesSimulator implements ExternalServicesSimulatorInterface {

    private static final Logger log = LoggerFactory.getLogger(DisabledExternalServicesSimulator.class);

    public DisabledExternalServicesSimulator() {
        log.info("🚫 ExternalServicesSimulator is DISABLED - no external events will be sent");
    }

    @Override
    public void simulatePaymentProcessing(String orderId, String customerId, BigDecimal amount) {
        log.debug("💳 Payment simulation disabled for order: {}", orderId);
        // Não faz nada quando desabilitado
    }

    @Override
    public void simulateSubscriptionAnalysis(String orderId, String customerId, String insuranceType, BigDecimal amount) {
        log.debug("📋 Subscription simulation disabled for order: {}", orderId);
        // Não faz nada quando desabilitado
    }

    @Override
    public void simulatePaymentAndSubscriptionProcessing(Order order) {
        log.debug("🔄 Payment and subscription simulation disabled for order: {}", order.getId().getValue());
        // Não faz nada quando desabilitado
    }
}
