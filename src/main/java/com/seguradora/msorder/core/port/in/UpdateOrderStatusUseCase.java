package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;

/**
 * Port de entrada para atualização de status de pedidos
 */
public interface UpdateOrderStatusUseCase {

    /**
     * Aprova um pedido
     */
    Order approveOrder(ApproveOrderCommand command);

    /**
     * Rejeita um pedido
     */
    Order rejectOrder(RejectOrderCommand command);

    /**
     * Cancela um pedido
     */
    Order cancelOrder(CancelOrderCommand command);

    /**
     * Processa um pedido
     */
    Order pendingOrder(PendingOrderCommand command);

    record ApproveOrderCommand(OrderId orderId) {}
    record RejectOrderCommand(OrderId orderId) {}
    record CancelOrderCommand(OrderId orderId) {}
    record PendingOrderCommand(OrderId orderId) {}
}
