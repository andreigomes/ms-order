package com.seguradora.msorder.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para detalhes de subscrição
 */
public class SubscriptionDetails {

    @JsonProperty("subscriptionId")
    private String subscriptionId;

    @JsonProperty("analyst")
    private String analyst;

    @JsonProperty("analysisDate")
    private String analysisDate;

    @JsonProperty("comments")
    private String comments;

    // Getters e Setters
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getAnalyst() {
        return analyst;
    }

    public void setAnalyst(String analyst) {
        this.analyst = analyst;
    }

    public String getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(String analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
