package com.seguradora.msorder.infrastructure.adapter.out.persistence.entity;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.domain.valueobject.SalesChannel;
import com.seguradora.msorder.core.domain.valueobject.PaymentMethod;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Entidade JPA para persistência de pedidos
 */
@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private InsuranceType category;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_channel", nullable = false)
    private SalesChannel salesChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "total_monthly_premium_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalMonthlyPremiumAmount;

    @Column(name = "insured_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal insuredAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coverages")
    private Map<String, BigDecimal> coverages;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "assistances")
    private List<String> assistances;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "history")
    private List<Map<String, Object>> history;

    // Campos para coordenação de eventos
    @Column(name = "payment_approved")
    private Boolean paymentApproved;

    @Column(name = "subscription_approved")
    private Boolean subscriptionApproved;

    // Construtor padrão
    public OrderJpaEntity() {}

    // Construtor completo
    public OrderJpaEntity(String id, String customerId, String productId, InsuranceType category,
                         SalesChannel salesChannel, PaymentMethod paymentMethod,
                         BigDecimal totalMonthlyPremiumAmount, BigDecimal insuredAmount,
                         Map<String, BigDecimal> coverages, List<String> assistances,
                         OrderStatus status, String description, LocalDateTime createdAt,
                         LocalDateTime updatedAt, LocalDateTime finishedAt,
                         List<Map<String, Object>> history, Boolean paymentApproved,
                         Boolean subscriptionApproved) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.category = category;
        this.salesChannel = salesChannel;
        this.paymentMethod = paymentMethod;
        this.totalMonthlyPremiumAmount = totalMonthlyPremiumAmount;
        this.insuredAmount = insuredAmount;
        this.coverages = coverages;
        this.assistances = assistances;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.finishedAt = finishedAt;
        this.history = history;
        this.paymentApproved = paymentApproved;
        this.subscriptionApproved = subscriptionApproved;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public InsuranceType getCategory() { return category; }
    public void setCategory(InsuranceType category) { this.category = category; }

    public SalesChannel getSalesChannel() { return salesChannel; }
    public void setSalesChannel(SalesChannel salesChannel) { this.salesChannel = salesChannel; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getTotalMonthlyPremiumAmount() { return totalMonthlyPremiumAmount; }
    public void setTotalMonthlyPremiumAmount(BigDecimal totalMonthlyPremiumAmount) { this.totalMonthlyPremiumAmount = totalMonthlyPremiumAmount; }

    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }

    public Map<String, BigDecimal> getCoverages() { return coverages; }
    public void setCoverages(Map<String, BigDecimal> coverages) { this.coverages = coverages; }

    public List<String> getAssistances() { return assistances; }
    public void setAssistances(List<String> assistances) { this.assistances = assistances; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public List<Map<String, Object>> getHistory() { return history; }
    public void setHistory(List<Map<String, Object>> history) { this.history = history; }

    public Boolean getPaymentApproved() { return paymentApproved; }
    public void setPaymentApproved(Boolean paymentApproved) { this.paymentApproved = paymentApproved; }

    public Boolean getSubscriptionApproved() { return subscriptionApproved; }
    public void setSubscriptionApproved(Boolean subscriptionApproved) { this.subscriptionApproved = subscriptionApproved; }
}
