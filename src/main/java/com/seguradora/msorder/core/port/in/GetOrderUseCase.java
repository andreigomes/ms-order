package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;

/**
 * Port de entrada para consulta de pedidos
 */
public interface GetOrderUseCase {

    /**
     * Busca um pedido por ID
     * @param query dados da consulta
     * @return pedido encontrado
     */
    Order getOrderById(GetOrderQuery query);

    /**
     * Query object para busca de pedido por ID
     */
    record GetOrderQuery(OrderId orderId) {}
}
