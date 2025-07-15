package com.seguradora.msorder.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * DTO para detalhes de pagamento
 */
public class PaymentDetails {

    @JsonProperty("paymentId")
    private String paymentId;

    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("processedAt")
    private String processedAt;

    // Getters e Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }
}
