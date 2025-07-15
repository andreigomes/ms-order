package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.in.ListOrdersUseCase;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementação do caso de uso para listagem de pedidos
 */
@Service
@Transactional(readOnly = true)
public class ListOrdersService implements ListOrdersUseCase {

    private final OrderRepositoryPort orderRepository;

    public ListOrdersService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getOrdersByCustomer(GetOrdersByCustomerQuery query) {
        return orderRepository.findByCustomerId(query.customerId());
    }

    @Override
    public List<Order> getOrdersByStatus(GetOrdersByStatusQuery query) {
        return orderRepository.findByStatus(query.status());
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
