package com.seguradora.msorder.core.domain.entity;

import com.seguradora.msorder.core.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio Order seguindo princípios de DDD
 */
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private InsuranceType insuranceType;
    private OrderStatus status;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor privado para garantir criação através de factory methods
    private Order() {}

    public static Order create(CustomerId customerId, InsuranceType insuranceType,
                              BigDecimal amount, String description) {
        validateCreateParameters(customerId, insuranceType, amount);

        Order order = new Order();
        order.id = OrderId.generate();
        order.customerId = customerId;
        order.insuranceType = insuranceType;
        order.status = OrderStatus.RECEIVED; // Estado inicial quando solicitação é criada
        order.amount = amount;
        order.description = description;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();

        return order;
    }

    public static Order restore(OrderId id, CustomerId customerId, InsuranceType insuranceType,
                               OrderStatus status, BigDecimal amount, String description,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        Order order = new Order();
        order.id = id;
        order.customerId = customerId;
        order.insuranceType = insuranceType;
        order.status = status;
        order.amount = amount;
        order.description = description;
        order.createdAt = createdAt;
        order.updatedAt = updatedAt;

        return order;
    }

    private static void validateCreateParameters(CustomerId customerId, InsuranceType insuranceType, BigDecimal amount) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(insuranceType, "Insurance type cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    /**
     * Atualiza o status da ordem validando as regras de transição
     */
    public void updateStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid state transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Aprova a solicitação (pagamento e subscrição confirmados)
     */
    public void approve() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Can only approve orders in PENDING state");
        }
        updateStatus(OrderStatus.APPROVED);
    }

    /**
     * Marca como pendente (aguardando pagamento e subscrição)
     */
    public void markAsPending() {
        if (status != OrderStatus.VALIDATED) {
            throw new IllegalStateException("Can only mark as pending orders in VALIDATED state");
        }
        updateStatus(OrderStatus.PENDING);
    }

    /**
     * Valida a solicitação após análise de fraudes
     */
    public void validate() {
        if (status != OrderStatus.RECEIVED) {
            throw new IllegalStateException("Can only validate orders in RECEIVED state");
        }
        updateStatus(OrderStatus.VALIDATED);
    }

    /**
     * Rejeita a solicitação
     */
    public void reject() {
        updateStatus(OrderStatus.REJECTED);
    }

    /**
     * Cancela a solicitação (apenas se permitido)
     */
    public void cancel() {
        if (status == OrderStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel approved order");
        }
        updateStatus(OrderStatus.CANCELLED);
    }

    // Getters
    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public InsuranceType getInsuranceType() { return insuranceType; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

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
