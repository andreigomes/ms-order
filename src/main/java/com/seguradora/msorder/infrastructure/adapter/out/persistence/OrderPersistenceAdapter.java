
/**
 * Adaptador de persistência que implementa a porta de saída do repositório
 */
@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    public OrderPersistenceAdapter(OrderJpaRepository jpaRepository, OrderPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity jpaEntity = mapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.restoreDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findById(orderId.getValue())
            .map(mapper::restoreDomain);
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.getValue())
            .stream()
            .map(mapper::restoreDomain)
            .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaRepository.findByStatus(status)
            .stream()
            .map(mapper::restoreDomain)
            .toList();
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(mapper::restoreDomain)
            .toList();
    }

    @Override
    public void deleteById(OrderId orderId) {
        jpaRepository.deleteById(orderId.getValue());
    }

    @Override
    public boolean existsById(OrderId orderId) {
        return jpaRepository.existsById(orderId.getValue());
    }
}
package com.seguradora.msorder.infrastructure.adapter.out.persistence;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
