package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.core.domain.entity.Order;

/**
 * Port para publicação de eventos relacionados a pedidos
 */
public interface OrderEventPublisherPort {

    void publishOrderCreated(Order order);

    void publishOrderValidated(Order order);

    void publishOrderPending(Order order);

    void publishOrderApproved(Order order);

    void publishOrderRejected(Order order);

    void publishOrderCancelled(Order order);

    void publishPaymentApproved(Order order);

    void publishSubscriptionApproved(Order order);
}
