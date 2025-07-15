package com.seguradora.msorder.application.mapper;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre DTOs e entidades de domínio
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customerId", expression = "java(new CustomerId(request.customerId()))")
    @Mapping(target = "productId", expression = "java(ProductId.of(request.productId()))")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "salesChannel", source = "salesChannel")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "totalMonthlyPremiumAmount", source = "totalMonthlyPremiumAmount")
    @Mapping(target = "insuredAmount", source = "insuredAmount")
    @Mapping(target = "coverages", expression = "java(Coverages.of(request.coverages()))")
    @Mapping(target = "assistances", expression = "java(Assistances.of(request.assistances()))")
    @Mapping(target = "description", source = "description")
    CreateOrderCommand toCreateCommand(CreateOrderRequest request);

    @Mapping(target = "id", expression = "java(order.getId().getValue().toString())")
    @Mapping(target = "customerId", expression = "java(order.getCustomerId().getValue())")
    @Mapping(target = "productId", expression = "java(order.getProductId() != null ? order.getProductId().getValue() : null)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "salesChannel", source = "salesChannel")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "totalMonthlyPremiumAmount", source = "totalMonthlyPremiumAmount")
    @Mapping(target = "insuredAmount", source = "insuredAmount")
    @Mapping(target = "coverages", expression = "java(order.getCoverages() != null ? order.getCoverages().getCoverageMap() : null)")
    @Mapping(target = "assistances", expression = "java(order.getAssistances() != null ? order.getAssistances().getAssistanceList() : null)")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "finishedAt", source = "finishedAt")
    @Mapping(target = "history", expression = "java(mapHistory(order))")
    OrderResponse toResponse(Order order);

    default java.util.List<OrderResponse.OrderHistoryResponse> mapHistory(Order order) {
        if (order.getHistory() == null || order.getHistory().getEntries() == null) {
            return java.util.Collections.emptyList();
        }

        return order.getHistory().getEntries().stream()
            .map(entry -> new OrderResponse.OrderHistoryResponse(
                entry.getToStatus(),
                entry.getTimestamp()
            ))
            .toList();
    }
}
