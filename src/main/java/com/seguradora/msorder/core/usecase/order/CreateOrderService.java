package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do caso de uso para criação de pedidos
 */
@Service
@Transactional
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public CreateOrderService(OrderRepositoryPort orderRepository,
                             OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order createOrder(CreateOrderCommand command) {
        // Criar o pedido usando factory method da entidade
        Order order = Order.create(
            command.customerId(),
            command.insuranceType(),
            command.amount(),
            command.description()
        );

        // Persistir o pedido
        Order savedOrder = orderRepository.save(order);

        // Publicar evento de criação
        eventPublisher.publishOrderCreated(savedOrder);

        return savedOrder;
    }
}
