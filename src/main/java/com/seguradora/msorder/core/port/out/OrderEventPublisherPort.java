package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.core.domain.entity.Order;

/**
 * Port para publicação de eventos de pedidos
 */
public interface OrderEventPublisherPort {

    void publishOrderCreated(Order order);

    void publishOrderApproved(Order order);

    void publishOrderRejected(Order order);

    void publishOrderRejected(Order order, String reason);

    void publishOrderCancelled(Order order);

    void publishOrderCompleted(Order order);

    void publishOrderPendingAnalysis(Order order, String riskLevel);
}
