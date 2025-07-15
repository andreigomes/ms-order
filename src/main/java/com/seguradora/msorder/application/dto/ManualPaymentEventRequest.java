package com.seguradora.msorder.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para simulação manual de eventos de pagamento
 */
public record ManualPaymentEventRequest(
    @NotBlank(message = "Order ID is required")
    String orderId,

    @NotNull(message = "Status is required")
    PaymentStatus status,

    String reason
) {
    public enum PaymentStatus {
        APPROVED, REJECTED
    }
}
