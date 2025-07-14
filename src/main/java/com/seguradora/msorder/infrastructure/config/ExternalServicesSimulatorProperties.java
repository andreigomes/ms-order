package com.seguradora.msorder.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de configuração para o simulador de serviços externos
 */
@Component
@ConfigurationProperties(prefix = "app.simulator")
public class ExternalServicesSimulatorProperties {

    private boolean enabled = false;
    private String fixedPaymentStatus = null; // APPROVED ou REJECTED
    private String fixedSubscriptionStatus = null; // APPROVED ou REJECTED
    private int paymentDelaySeconds = 2;
    private int subscriptionDelaySeconds = 3;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFixedPaymentStatus() {
        return fixedPaymentStatus;
    }

    public void setFixedPaymentStatus(String fixedPaymentStatus) {
        this.fixedPaymentStatus = fixedPaymentStatus;
    }

    public String getFixedSubscriptionStatus() {
        return fixedSubscriptionStatus;
    }

    public void setFixedSubscriptionStatus(String fixedSubscriptionStatus) {
        this.fixedSubscriptionStatus = fixedSubscriptionStatus;
    }

    public int getPaymentDelaySeconds() {
        return paymentDelaySeconds;
    }

    public void setPaymentDelaySeconds(int paymentDelaySeconds) {
        this.paymentDelaySeconds = paymentDelaySeconds;
    }

    public int getSubscriptionDelaySeconds() {
        return subscriptionDelaySeconds;
    }

    public void setSubscriptionDelaySeconds(int subscriptionDelaySeconds) {
        this.subscriptionDelaySeconds = subscriptionDelaySeconds;
    }
}
