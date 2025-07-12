package com.seguradora.msorder.infrastructure.adapter.out.persistence.mapper;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre entidades de domínio e entidades JPA
 */
@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    @Mapping(target = "id", expression = "java(order.getId().getValue())")
    @Mapping(target = "customerId", expression = "java(order.getCustomerId().getValue())")
    OrderJpaEntity toJpaEntity(Order order);

    default Order restoreDomain(OrderJpaEntity entity) {
        return Order.restore(
            new OrderId(entity.getId()),
            new CustomerId(entity.getCustomerId()),
            entity.getInsuranceType(),
            entity.getStatus(),
            entity.getAmount(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
