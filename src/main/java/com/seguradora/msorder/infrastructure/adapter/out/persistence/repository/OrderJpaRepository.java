package com.seguradora.msorder.infrastructure.adapter.out.persistence.repository;

import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para entidade Order
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {

    List<OrderJpaEntity> findByCustomerId(String customerId);

    List<OrderJpaEntity> findByStatus(OrderStatus status);
}
