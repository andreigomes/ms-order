package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.domain.valueobject.SalesChannel;
import com.seguradora.msorder.core.domain.valueobject.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO de resposta para operações de pedidos conforme especificação da API
 */
public record OrderResponse(
    String id,
    String customerId,
    String productId,
    InsuranceType category,
    SalesChannel salesChannel,
    PaymentMethod paymentMethod,
    BigDecimal totalMonthlyPremiumAmount,
    BigDecimal insuredAmount,
    Map<String, BigDecimal> coverages,
    List<String> assistances,
    OrderStatus status,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime finishedAt,
    List<OrderHistoryResponse> history
) {
    /**
     * DTO para representar o histórico de mudanças de status
     */
    public record OrderHistoryResponse(
        OrderStatus status,
        LocalDateTime timestamp
    ) {}
}
