package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.port.in.UpdateOrderStatusUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação otimizada do caso de uso para atualização de status de pedidos
 */
@Service
@Transactional
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public UpdateOrderStatusService(OrderRepositoryPort orderRepository,
                                   OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @CacheEvict(value = "orders", key = "#command.orderId().value")
    public Order approveOrder(ApproveOrderCommand command) {
        Order order = findOrderById(command.orderId());
        order.approve();
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderApproved(savedOrder);
        return savedOrder;
    }

    @Override
    @CacheEvict(value = "orders", key = "#command.orderId().value")
    public Order rejectOrder(RejectOrderCommand command) {
        Order order = findOrderById(command.orderId());
        order.reject();
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderRejected(savedOrder);
        return savedOrder;
    }

    @Override
    @CacheEvict(value = "orders", key = "#command.orderId().value")
    public Order cancelOrder(CancelOrderCommand command) {
        Order order = findOrderById(command.orderId());
        order.cancel();
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderCancelled(savedOrder);
        return savedOrder;
    }

    @Override
    public Order pendingOrder(PendingOrderCommand command) {
        Order order = findOrderById(command.orderId());
        order.markAsPending();
        return orderRepository.save(order);
    }

    private Order findOrderById(OrderId orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new GetOrderService.OrderNotFoundException("Order not found with ID: " + orderId));
    }
}
