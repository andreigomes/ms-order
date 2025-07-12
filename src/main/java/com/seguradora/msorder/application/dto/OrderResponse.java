package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de pedidos
 */
public record OrderResponse(
    String id,
    String customerId,
    InsuranceType insuranceType,
    OrderStatus status,
    BigDecimal amount,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
