package com.seguradora.msorder.core.domain.valueobject;

/**
 * Enum para representar os métodos de pagamento disponíveis
 */
public enum PaymentMethod {
    CREDIT_CARD("Cartão de Crédito"),
    DEBIT_ACCOUNT("Débito em Conta"),
    BOLETO("Boleto"),
    PIX("PIX"),
    BANK_TRANSFER("Transferência Bancária");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name();
    }
}
