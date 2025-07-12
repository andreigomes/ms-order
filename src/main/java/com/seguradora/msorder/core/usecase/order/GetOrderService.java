package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.in.GetOrderUseCase;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do caso de uso para consulta de pedidos
 */
@Service
@Transactional(readOnly = true)
public class GetOrderService implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public GetOrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getOrderById(GetOrderQuery query) {
        return orderRepository.findById(query.orderId())
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + query.orderId()));
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }
}
