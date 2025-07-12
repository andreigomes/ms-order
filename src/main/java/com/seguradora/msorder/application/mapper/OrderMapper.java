package com.seguradora.msorder.application.mapper;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre DTOs e entidades de domínio
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customerId", expression = "java(new CustomerId(request.customerId()))")
    CreateOrderCommand toCreateCommand(CreateOrderRequest request);

    @Mapping(target = "id", expression = "java(order.getId().toString())")
    @Mapping(target = "customerId", expression = "java(order.getCustomerId().toString())")
    OrderResponse toResponse(Order order);
}
