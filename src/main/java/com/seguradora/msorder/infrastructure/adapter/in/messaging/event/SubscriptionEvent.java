package com.seguradora.msorder.infrastructure.adapter.in.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de subscrição recebido do serviço de subscrição/underwriting
 */
public record SubscriptionEvent(
    String orderId,
    String customerId,
    String insuranceType,
    BigDecimal amount,
    SubscriptionStatus status,
    String reason,
    String riskLevel,
    BigDecimal premiumAdjustment,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime processedAt
) {
    public enum SubscriptionStatus {
        APPROVED,
        REJECTED
    }
}
