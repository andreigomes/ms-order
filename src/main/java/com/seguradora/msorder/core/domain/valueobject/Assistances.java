package com.seguradora.msorder.core.domain.valueobject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value Object para representar as assistências de uma apólice
 */
public class Assistances {
    private final List<String> assistanceList;

    private Assistances(List<String> assistanceList) {
        this.assistanceList = Collections.unmodifiableList(Objects.requireNonNull(assistanceList, "Assistance list cannot be null"));
    }

    public static Assistances of(List<String> assistanceList) {
        if (assistanceList == null || assistanceList.isEmpty()) {
            throw new IllegalArgumentException("Assistance list cannot be null or empty");
        }

        // Validar que não há valores nulos ou vazios
        assistanceList.forEach(assistance -> {
            if (assistance == null || assistance.trim().isEmpty()) {
                throw new IllegalArgumentException("Assistance cannot be null or empty");
            }
        });

        return new Assistances(assistanceList);
    }

    public List<String> getAssistanceList() {
        return assistanceList;
    }

    public int getCount() {
        return assistanceList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assistances that = (Assistances) o;
        return Objects.equals(assistanceList, that.assistanceList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assistanceList);
    }

    @Override
    public String toString() {
        return "Assistances{" + assistanceList + '}';
    }
}
