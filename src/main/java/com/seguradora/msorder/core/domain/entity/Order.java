package com.seguradora.msorder.core.domain.entity;

import com.seguradora.msorder.core.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Entidade de domínio Order seguindo princípios de DDD
 */
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private ProductId productId;
    private InsuranceType category; // Renomeado de insuranceType para category
    private SalesChannel salesChannel;
    private PaymentMethod paymentMethod;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private Coverages coverages;
    private Assistances assistances;
    private OrderStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime finishedAt;
    private OrderHistory history;

    // Campos para coordenação de eventos
    private Boolean paymentApproved = null; // null = não processado, true = aprovado, false = rejeitado
    private Boolean subscriptionApproved = null; // null = não processado, true = aprovado, false = rejeitado

    // Construtor privado para garantir criação através de factory methods
    private Order() {}

    public static Order create(CustomerId customerId, ProductId productId, InsuranceType category,
                              SalesChannel salesChannel, PaymentMethod paymentMethod,
                              BigDecimal totalMonthlyPremiumAmount, BigDecimal insuredAmount,
                              Coverages coverages, Assistances assistances, String description) {

        validateCreateParameters(customerId, productId, category, salesChannel, paymentMethod,
                               totalMonthlyPremiumAmount, insuredAmount, coverages, assistances);

        Order order = new Order();
        order.id = OrderId.generate();
        order.customerId = customerId;
        order.productId = productId;
        order.category = category;
        order.salesChannel = salesChannel;
        order.paymentMethod = paymentMethod;
        order.totalMonthlyPremiumAmount = totalMonthlyPremiumAmount;
        order.insuredAmount = insuredAmount;
        order.coverages = coverages;
        order.assistances = assistances;
        order.status = OrderStatus.RECEIVED; // Estado inicial quando solicitação é criada
        order.description = description;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        order.history = OrderHistory.empty().addEntry(null, OrderStatus.RECEIVED, "Pedido criado");

        return order;
    }

    public static Order restore(OrderId id, CustomerId customerId, ProductId productId, InsuranceType category,
                               SalesChannel salesChannel, PaymentMethod paymentMethod,
                               BigDecimal totalMonthlyPremiumAmount, BigDecimal insuredAmount,
                               Coverages coverages, Assistances assistances, OrderStatus status,
                               String description, LocalDateTime createdAt, LocalDateTime updatedAt,
                               LocalDateTime finishedAt, OrderHistory history, Boolean paymentApproved, Boolean subscriptionApproved) {
        Order order = new Order();
        order.id = id;
        order.customerId = customerId;
        order.productId = productId;
        order.category = category;
        order.salesChannel = salesChannel;
        order.paymentMethod = paymentMethod;
        order.totalMonthlyPremiumAmount = totalMonthlyPremiumAmount;
        order.insuredAmount = insuredAmount;
        order.coverages = coverages;
        order.assistances = assistances;
        order.status = status;
        order.description = description;
        order.createdAt = createdAt;
        order.updatedAt = updatedAt;
        order.finishedAt = finishedAt;
        order.history = history != null ? history : OrderHistory.empty();
        order.paymentApproved = paymentApproved;
        order.subscriptionApproved = subscriptionApproved;

        return order;
    }

    private static void validateCreateParameters(CustomerId customerId, ProductId productId, InsuranceType category,
                                               SalesChannel salesChannel, PaymentMethod paymentMethod,
                                               BigDecimal totalMonthlyPremiumAmount, BigDecimal insuredAmount,
                                               Coverages coverages, Assistances assistances) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(category, "Category cannot be null");
        Objects.requireNonNull(salesChannel, "Sales channel cannot be null");
        Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
        Objects.requireNonNull(totalMonthlyPremiumAmount, "Total monthly premium amount cannot be null");
        Objects.requireNonNull(insuredAmount, "Insured amount cannot be null");
        Objects.requireNonNull(coverages, "Coverages cannot be null");
        Objects.requireNonNull(assistances, "Assistances cannot be null");

        if (totalMonthlyPremiumAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total monthly premium amount must be greater than zero");
        }

        if (insuredAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Insured amount must be greater than zero");
        }
    }

    /**
     * Atualiza o status da ordem validando as regras de transição
     */
    public void updateStatus(OrderStatus newStatus) {
        updateStatus(newStatus, null);
    }

    /**
     * Atualiza o status da ordem validando as regras de transição com motivo
     */
    public void updateStatus(OrderStatus newStatus, String reason) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid state transition from %s to %s", this.status, newStatus)
            );
        }

        OrderStatus previousStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        // Adicionar entrada no histórico
        this.history = this.history.addEntry(previousStatus, newStatus, reason);

        // Definir data de finalização para estados finais
        if (newStatus == OrderStatus.APPROVED || newStatus == OrderStatus.REJECTED || newStatus == OrderStatus.CANCELLED) {
            this.finishedAt = LocalDateTime.now();
        }
    }

    /**
     * Aprova a solicitação (pagamento e subscrição confirmados)
     */
    public void approve() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only approve orders in PENDING state");
        }
        updateStatus(OrderStatus.APPROVED, "Pagamento e subscrição aprovados");
    }

    /**
     * Marca como pendente (aguardando pagamento e subscrição)
     */
    public void markAsPending() {
        if (status != OrderStatus.VALIDATED) {
            throw new IllegalStateException("Can only mark as pending orders in VALIDATED state");
        }
        updateStatus(OrderStatus.PENDING, "Aguardando confirmação de pagamento e subscrição");
    }

    /**
     * Valida a solicitação após análise de fraudes
     */
    public void validate() {
        if (status != OrderStatus.RECEIVED) {
            throw new IllegalStateException("Can only validate orders in RECEIVED state");
        }
        updateStatus(OrderStatus.VALIDATED, "Validação de fraudes concluída");
    }

    /**
     * Rejeita a solicitação
     */
    public void reject() {
        reject("Solicitação rejeitada");
    }

    /**
     * Rejeita a solicitação com motivo específico
     */
    public void reject(String reason) {
        updateStatus(OrderStatus.REJECTED, reason);
    }

    /**
     * Cancela a solicitação (apenas se permitido)
     */
    public void cancel() {
        cancel("Cancelamento solicitado pelo cliente");
    }

    /**
     * Cancela a solicitação com motivo específico
     */
    public void cancel(String reason) {
        if (status == OrderStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel approved order");
        }
        updateStatus(OrderStatus.CANCELLED, reason);
    }

    /**
     * Registra aprovação de pagamento e verifica se pode finalizar
     */
    public boolean approvePayment() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only approve payment for orders in PENDING state");
        }

        this.paymentApproved = true;
        this.updatedAt = LocalDateTime.now();
        this.history = this.history.addEntry(status, status, "Pagamento aprovado");

        return canBeFinalized();
    }

    /**
     * Registra rejeição de pagamento
     */
    public void rejectPayment(String reason) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only reject payment for orders in PENDING state");
        }

        this.paymentApproved = false;
        reject("Pagamento rejeitado: " + reason);
    }

    /**
     * Registra aprovação de subscrição e verifica se pode finalizar
     */
    public boolean approveSubscription() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only approve subscription for orders in PENDING state");
        }

        this.subscriptionApproved = true;
        this.updatedAt = LocalDateTime.now();
        this.history = this.history.addEntry(status, status, "Subscrição aprovada");

        return canBeFinalized();
    }

    /**
     * Registra rejeição de subscrição
     */
    public void rejectSubscription(String reason) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only reject subscription for orders in PENDING state");
        }

        this.subscriptionApproved = false;
        reject("Subscrição rejeitada: " + reason);
    }

    /**
     * Verifica se o pedido pode ser finalizado (ambos pagamento e subscrição aprovados)
     */
    public boolean canBeFinalized() {
        return Boolean.TRUE.equals(paymentApproved) && Boolean.TRUE.equals(subscriptionApproved);
    }

    /**
     * Verifica se algum processo foi rejeitado
     */
    public boolean hasAnyRejection() {
        return Boolean.FALSE.equals(paymentApproved) || Boolean.FALSE.equals(subscriptionApproved);
    }

    /**
     * Finaliza o pedido quando ambos pagamento e subscrição foram aprovados
     */
    public void finalizeApproval() {
        if (!canBeFinalized()) {
            throw new IllegalStateException("Cannot finalize order - payment and subscription must both be approved");
        }

        updateStatus(OrderStatus.APPROVED, "Pagamento e subscrição aprovados - pedido finalizado");
    }

    // Getters
    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public ProductId getProductId() { return productId; }
    public InsuranceType getCategory() { return category; }
    public SalesChannel getSalesChannel() { return salesChannel; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public BigDecimal getTotalMonthlyPremiumAmount() { return totalMonthlyPremiumAmount; }
    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public Coverages getCoverages() { return coverages; }
    public Assistances getAssistances() { return assistances; }
    public OrderStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public OrderHistory getHistory() { return history; }
    public Boolean getPaymentApproved() { return paymentApproved; }
    public Boolean getSubscriptionApproved() { return subscriptionApproved; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
