package com.seguradora.msorder.core.domain.entity;

import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidData() {
        // Given
        CustomerId customerId = new CustomerId("CUST001");
        InsuranceType insuranceType = InsuranceType.AUTO;
        BigDecimal amount = new BigDecimal("1500.00");
        String description = "Seguro auto para veículo modelo 2023";

        // When
        Order order = Order.create(customerId, insuranceType, amount, description);

        // Then
        assertNotNull(order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(insuranceType, order.getInsuranceType());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(amount, order.getAmount());
        assertEquals(description, order.getDescription());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullCustomerId() {
        // Given
        InsuranceType insuranceType = InsuranceType.AUTO;
        BigDecimal amount = new BigDecimal("1500.00");

        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(null, insuranceType, amount, "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNegativeAmount() {
        // Given
        CustomerId customerId = new CustomerId("CUST001");
        InsuranceType insuranceType = InsuranceType.AUTO;
        BigDecimal amount = new BigDecimal("-100.00");

        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(customerId, insuranceType, amount, "Description"));
    }

    @Test
    void shouldApproveOrderWhenStatusIsPending() {
        // Given
        Order order = createValidOrder();

        // When
        order.approve();

        // Then
        assertEquals(OrderStatus.APPROVED, order.getStatus());
    }

    @Test
    void shouldRejectOrderWhenStatusIsPending() {
        // Given
        Order order = createValidOrder();

        // When
        order.reject();

        // Then
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenApprovingCompletedOrder() {
        // Given
        Order order = createValidOrder();
        order.approve();
        order.complete();

        // When & Then
        assertThrows(IllegalStateException.class, order::approve);
    }

    @Test
    void shouldCancelOrderWhenStatusIsNotCompletedOrCancelled() {
        // Given
        Order order = createValidOrder();

        // When
        order.cancel();

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldProcessOrderWhenStatusIsPending() {
        // Given
        Order order = createValidOrder();

        // When
        order.process();

        // Then
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    void shouldCompleteOrderWhenStatusIsApproved() {
        // Given
        Order order = createValidOrder();
        order.approve();

        // When
        order.complete();

        // Then
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCompletingPendingOrder() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, order::complete);
    }

    private Order createValidOrder() {
        return Order.create(
            new CustomerId("CUST001"),
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Test order"
        );
    }
}
