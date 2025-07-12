package com.seguradora.msorder.infrastructure.adapter.out.messaging.event;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento base para pedidos
 */
public record OrderEvent(
    String orderId,
    String customerId,
    InsuranceType insuranceType,
    OrderStatus status,
    BigDecimal amount,
    String description,
    LocalDateTime timestamp,
    String eventType
) {

    public static OrderEvent orderCreated(String orderId, String customerId, InsuranceType insuranceType,
                                         BigDecimal amount, String description) {
        return new OrderEvent(orderId, customerId, insuranceType, OrderStatus.PENDING,
                             amount, description, LocalDateTime.now(), "ORDER_CREATED");
    }

    public static OrderEvent orderApproved(String orderId, String customerId, InsuranceType insuranceType,
                                          BigDecimal amount, String description) {
        return new OrderEvent(orderId, customerId, insuranceType, OrderStatus.APPROVED,
                             amount, description, LocalDateTime.now(), "ORDER_APPROVED");
    }

    public static OrderEvent orderRejected(String orderId, String customerId, InsuranceType insuranceType,
                                          BigDecimal amount, String description) {
        return new OrderEvent(orderId, customerId, insuranceType, OrderStatus.REJECTED,
                             amount, description, LocalDateTime.now(), "ORDER_REJECTED");
    }

    public static OrderEvent orderCancelled(String orderId, String customerId, InsuranceType insuranceType,
                                           BigDecimal amount, String description) {
        return new OrderEvent(orderId, customerId, insuranceType, OrderStatus.CANCELLED,
                             amount, description, LocalDateTime.now(), "ORDER_CANCELLED");
    }

    public static OrderEvent orderCompleted(String orderId, String customerId, InsuranceType insuranceType,
                                           BigDecimal amount, String description) {
        return new OrderEvent(orderId, customerId, insuranceType, OrderStatus.COMPLETED,
                             amount, description, LocalDateTime.now(), "ORDER_COMPLETED");
    }

    public static OrderEvent orderPendingAnalysis(String orderId, String customerId, InsuranceType insuranceType,
                                                 BigDecimal amount, String description) {
        return new OrderEvent(orderId, customerId, insuranceType, OrderStatus.PENDING_ANALYSIS,
                             amount, description, LocalDateTime.now(), "ORDER_PENDING_ANALYSIS");
    }
}
