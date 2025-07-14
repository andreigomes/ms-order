package com.seguradora.msorder.core.domain.valueobject;

/**
 * Enum para representar os canais de vendas disponíveis
 */
public enum SalesChannel {
    MOBILE("Mobile"),
    WHATSAPP("WhatsApp"),
    WEB_SITE("Web Site"),
    PHONE("Phone"),
    BRANCH("Branch"),
    PARTNER("Partner");

    private final String description;

    SalesChannel(String description) {
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
