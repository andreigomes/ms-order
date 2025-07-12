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
        order.status = OrderStatus.PENDING;
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

    public void approve() {
        if (status != OrderStatus.PENDING && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot approve order with status: " + status);
        }
        this.status = OrderStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        if (status != OrderStatus.PENDING && status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot reject order with status: " + status);
        }
        this.status = OrderStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void process() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot process order with status: " + status);
        }
        this.status = OrderStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Cannot complete order with status: " + status);
        }
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
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
