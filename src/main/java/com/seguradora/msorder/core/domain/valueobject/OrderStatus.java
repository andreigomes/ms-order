package com.seguradora.msorder.core.domain.valueobject;

public enum OrderStatus {
    PENDING("Pendente"),
    PROCESSING("Processando"),
    APPROVED("Aprovado"),
    REJECTED("Rejeitado"),
    CANCELLED("Cancelado"),
    COMPLETED("Concluído");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
