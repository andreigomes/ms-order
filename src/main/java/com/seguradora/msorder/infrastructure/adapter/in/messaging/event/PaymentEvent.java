package com.seguradora.msorder.infrastructure.adapter.in.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de pagamento recebido do serviço de pagamentos
 */
public record PaymentEvent(
    String orderId,
    String customerId,
    BigDecimal amount,
    String paymentMethod,
    PaymentStatus status,
    String transactionId,
    String reason,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime processedAt
) {
    public enum PaymentStatus {
        APPROVED,
        REJECTED
    }
}
