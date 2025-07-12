package com.seguradora.msorder.core.domain.valueobject;

public enum OrderStatus {
    PENDING("Pendente"),
    PROCESSING("Processando"),
    PENDING_PAYMENT("Aguardando Pagamento"),
    PENDING_ANALYSIS("Aguardando Análise"),
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
