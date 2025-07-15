package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port para persistência de pedidos
 */
public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    List<Order> findByCustomerId(CustomerId customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();

    void deleteById(OrderId orderId);

    boolean existsById(OrderId orderId);
}
