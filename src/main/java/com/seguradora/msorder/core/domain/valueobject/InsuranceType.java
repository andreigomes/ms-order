package com.seguradora.msorder.core.domain.valueobject;

public enum InsuranceType {
    AUTO("Seguro Automóvel"),
    HOME("Seguro Residencial"),
    LIFE("Seguro de Vida"),
    HEALTH("Seguro Saúde"),
    TRAVEL("Seguro Viagem"),
    BUSINESS("Seguro Empresarial");

    private final String description;

    InsuranceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
