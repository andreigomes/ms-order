package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;

import java.util.List;

/**
 * Port de entrada para listagem de pedidos
 */
public interface ListOrdersUseCase {

    /**
     * Lista pedidos por cliente
     */
    List<Order> getOrdersByCustomer(GetOrdersByCustomerQuery query);

    /**
     * Lista pedidos por status
     */
    List<Order> getOrdersByStatus(GetOrdersByStatusQuery query);

    /**
     * Lista todos os pedidos
     */
    List<Order> getAllOrders();

    record GetOrdersByCustomerQuery(CustomerId customerId) {}
    record GetOrdersByStatusQuery(OrderStatus status) {}
}
