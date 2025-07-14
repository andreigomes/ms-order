package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta para criação de pedidos
 */
public record CreateOrderResponse(
    String orderId,
    String customerId,
    BigDecimal amount,
    InsuranceType insuranceType,
    String description,
    OrderStatus status,
    LocalDateTime createdAt,
    String message
) {
}
