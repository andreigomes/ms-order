package com.seguradora.msorder.core.domain.valueobject;

/**
 * Estados do pedido conforme regras de negócio da seguradora
 */
public enum OrderStatus {
    RECEIVED("Received"),           // Estado inicial quando recebido
    VALIDATED("Validated"),         // Passou na análise de fraudes
    PENDING("Pending"),             // Aguarda pagamento e subscrição
    APPROVED("Approved"),           // Pagamento e subscrição confirmados
    REJECTED("Rejected"),           // Rejeitado por fraude, pagamento ou subscrição
    CANCELLED("Cancelled");         // Cancelado pelo cliente

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Valida se é possível transicionar do estado atual para o novo estado
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case RECEIVED -> newStatus == VALIDATED || newStatus == REJECTED || newStatus == CANCELLED;
            case VALIDATED -> newStatus == PENDING || newStatus == CANCELLED || newStatus == REJECTED;
            case PENDING -> newStatus == APPROVED || newStatus == REJECTED || newStatus == CANCELLED;
            case APPROVED -> newStatus == CANCELLED;
            case REJECTED -> false;
            case CANCELLED -> false;
        };
    }

    /**
     * Verifica se o pedido pode ser cancelado no estado atual
     */
    public boolean canBeCancelled() {
        return this == RECEIVED || this == VALIDATED || this == PENDING;
    }
}
