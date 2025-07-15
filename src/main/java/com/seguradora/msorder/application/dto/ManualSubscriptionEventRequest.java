package com.seguradora.msorder.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para simulação manual de eventos de subscrição
 */
public record ManualSubscriptionEventRequest(
    @NotBlank(message = "Order ID is required")
    String orderId,

    @NotNull(message = "Status is required")
    SubscriptionStatus status,

    String reason
) {
    public enum SubscriptionStatus {
        APPROVED, REJECTED
    }
}
