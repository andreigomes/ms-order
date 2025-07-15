package com.seguradora.msorder.infrastructure.adapter.out.persistence.mapper;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

/**
 * Mapper para conversão entre entidades de domínio e entidades JPA
 */
@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    @Mapping(target = "id", expression = "java(order.getId().getValue().toString())")
    @Mapping(target = "customerId", expression = "java(order.getCustomerId().getValue())")
    @Mapping(target = "productId", expression = "java(order.getProductId() != null ? order.getProductId().getValue() : null)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "salesChannel", source = "salesChannel")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "totalMonthlyPremiumAmount", source = "totalMonthlyPremiumAmount")
    @Mapping(target = "insuredAmount", source = "insuredAmount")
    @Mapping(target = "coverages", expression = "java(order.getCoverages().getCoverageMap())")
    @Mapping(target = "assistances", expression = "java(order.getAssistances().getAssistanceList())")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "finishedAt", source = "finishedAt")
    @Mapping(target = "history", expression = "java(mapHistoryToJson(order.getHistory()))")
    @Mapping(target = "paymentApproved", source = "paymentApproved")
    @Mapping(target = "subscriptionApproved", source = "subscriptionApproved")
    OrderJpaEntity toJpaEntity(Order order);

    default Order restoreDomain(OrderJpaEntity entity) {
        return Order.restore(
            OrderId.of(entity.getId()),
            new CustomerId(entity.getCustomerId()),
            entity.getProductId() != null ? ProductId.of(entity.getProductId()) : null,
            entity.getCategory(),
            entity.getSalesChannel(),
            entity.getPaymentMethod(),
            entity.getTotalMonthlyPremiumAmount(),
            entity.getInsuredAmount(),
            entity.getCoverages() != null ? Coverages.of(entity.getCoverages()) : null,
            entity.getAssistances() != null ? Assistances.of(entity.getAssistances()) : null,
            entity.getStatus(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getFinishedAt(),
            OrderHistory.fromJson(entity.getHistory()),
            entity.getPaymentApproved(),
            entity.getSubscriptionApproved(),
            entity.getVersion()
        );
    }

    default List<Map<String, Object>> mapHistoryToJson(OrderHistory history) {
        return history != null ? history.toJson() : null;
    }
}
