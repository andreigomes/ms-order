package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderResponseTest {

    @Test
    void shouldCreateCreateOrderResponseWithAllFields() {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        String customerId = "customer-123";
        BigDecimal amount = new BigDecimal("1500.50");
        InsuranceType insuranceType = InsuranceType.AUTO;
        String description = "Auto insurance policy";
        OrderStatus status = OrderStatus.RECEIVED;
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 15, 10, 30);
        String message = "Order created successfully";

        // When
        CreateOrderResponse response = new CreateOrderResponse(
            orderId, customerId, amount, insuranceType, description, status, createdAt, message
        );

        // Then
        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.amount()).isEqualTo(amount);
        assertThat(response.insuranceType()).isEqualTo(insuranceType);
        assertThat(response.description()).isEqualTo(description);
        assertThat(response.status()).isEqualTo(status);
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.message()).isEqualTo(message);
    }

    @Test
    void shouldCreateCreateOrderResponseWithNullValues() {
        // When
        CreateOrderResponse response = new CreateOrderResponse(
            null, null, null, null, null, null, null, null
        );

        // Then
        assertThat(response.orderId()).isNull();
        assertThat(response.customerId()).isNull();
        assertThat(response.amount()).isNull();
        assertThat(response.insuranceType()).isNull();
        assertThat(response.description()).isNull();
        assertThat(response.status()).isNull();
        assertThat(response.createdAt()).isNull();
        assertThat(response.message()).isNull();
    }

    @Test
    void shouldBeEqualWhenSameValues() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        CreateOrderResponse response1 = new CreateOrderResponse(
            "123", "customer-1", new BigDecimal("100.00"), InsuranceType.HOME,
            "Description", OrderStatus.RECEIVED, now, "Success"
        );
        CreateOrderResponse response2 = new CreateOrderResponse(
            "123", "customer-1", new BigDecimal("100.00"), InsuranceType.HOME,
            "Description", OrderStatus.RECEIVED, now, "Success"
        );

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValues() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        CreateOrderResponse response1 = new CreateOrderResponse(
            "123", "customer-1", new BigDecimal("100.00"), InsuranceType.HOME,
            "Description", OrderStatus.RECEIVED, now, "Success"
        );
        CreateOrderResponse response2 = new CreateOrderResponse(
            "456", "customer-1", new BigDecimal("100.00"), InsuranceType.HOME,
            "Description", OrderStatus.RECEIVED, now, "Success"
        );

        // Then
        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        CreateOrderResponse response = new CreateOrderResponse(
            "123", "customer-1", new BigDecimal("100.00"), InsuranceType.AUTO,
            "Test", OrderStatus.VALIDATED, LocalDateTime.now(), "OK"
        );

        // When
        String toString = response.toString();

        // Then
        assertThat(toString).contains("CreateOrderResponse");
        assertThat(toString).contains("123");
        assertThat(toString).contains("customer-1");
        assertThat(toString).contains("100.00");
        assertThat(toString).contains("AUTO");
    }
}
