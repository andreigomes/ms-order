package com.seguradora.msorder.core.domain.valueobject;

/**
 * Enum que representa os níveis de risco do cliente na análise de fraudes
 */
public enum RiskLevel {
    REGULAR("Cliente com perfil de risco baixo"),
    HIGH_RISK("Cliente com perfil de risco alto"),
    PREFERENTIAL("Cliente preferencial com bom relacionamento"),
    NO_INFO("Cliente sem informações suficientes");

    private final String description;

    RiskLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converte uma string para RiskLevel, tratando diferentes formatos
     */
    public static RiskLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NO_INFO;
        }

        return switch (value.toUpperCase().trim()) {
            case "REGULAR" -> REGULAR;
            case "ALTO_RISCO", "HIGH_RISK" -> HIGH_RISK;
            case "PREFERENCIAL", "PREFERENTIAL" -> PREFERENTIAL;
            case "SEM_INFORMACAO", "NO_INFO" -> NO_INFO;
            default -> NO_INFO; // Default para casos não mapeados
        };
    }
}
