package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderResponseTest {

    @Test
    void shouldCreateOrderResponseWithAllFields() {
        // Given
        String id = "123e4567-e89b-12d3-a456-426614174000";
        String customerId = "customer-123";
        String productId = "product-456";
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        BigDecimal insuredAmount = new BigDecimal("50000.00");
        Map<String, BigDecimal> coverages = Map.of("collision", new BigDecimal("40000.00"));
        List<String> assistances = List.of("24h roadside assistance");
        OrderStatus status = OrderStatus.APPROVED;
        String description = "Auto insurance policy";
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 15, 11, 0);
        LocalDateTime finishedAt = LocalDateTime.of(2025, 1, 15, 12, 0);
        List<OrderResponse.OrderHistoryResponse> history = List.of(
            new OrderResponse.OrderHistoryResponse(OrderStatus.RECEIVED, createdAt),
            new OrderResponse.OrderHistoryResponse(OrderStatus.APPROVED, finishedAt)
        );

        // When
        OrderResponse response = new OrderResponse(
            id, customerId, productId, category, salesChannel, paymentMethod,
            totalMonthlyPremiumAmount, insuredAmount, coverages, assistances,
            status, description, createdAt, updatedAt, finishedAt, history
        );

        // Then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.productId()).isEqualTo(productId);
        assertThat(response.category()).isEqualTo(category);
        assertThat(response.salesChannel()).isEqualTo(salesChannel);
        assertThat(response.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(response.totalMonthlyPremiumAmount()).isEqualTo(totalMonthlyPremiumAmount);
        assertThat(response.insuredAmount()).isEqualTo(insuredAmount);
        assertThat(response.coverages()).isEqualTo(coverages);
        assertThat(response.assistances()).isEqualTo(assistances);
        assertThat(response.status()).isEqualTo(status);
        assertThat(response.description()).isEqualTo(description);
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(updatedAt);
        assertThat(response.finishedAt()).isEqualTo(finishedAt);
        assertThat(response.history()).isEqualTo(history);
    }

    @Test
    void shouldCreateOrderResponseWithNullOptionalFields() {
        // When
        OrderResponse response = new OrderResponse(
            "123", "customer-1", "product-1", InsuranceType.HOME, SalesChannel.MOBILE,
            PaymentMethod.PIX, new BigDecimal("100.00"), new BigDecimal("10000.00"),
            null, null, OrderStatus.PENDING, null, LocalDateTime.now(),
            LocalDateTime.now(), null, null
        );

        // Then
        assertThat(response.coverages()).isNull();
        assertThat(response.assistances()).isNull();
        assertThat(response.description()).isNull();
        assertThat(response.finishedAt()).isNull();
        assertThat(response.history()).isNull();
    }

    @Test
    void shouldBeEqualWhenSameValues() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        OrderResponse response1 = new OrderResponse(
            "123", "customer-1", "product-1", InsuranceType.LIFE, SalesChannel.PHONE,
            PaymentMethod.BOLETO, new BigDecimal("200.00"), new BigDecimal("20000.00"),
            Map.of(), List.of(), OrderStatus.VALIDATED, "Test", now, now, null, List.of()
        );
        OrderResponse response2 = new OrderResponse(
            "123", "customer-1", "product-1", InsuranceType.LIFE, SalesChannel.PHONE,
            PaymentMethod.BOLETO, new BigDecimal("200.00"), new BigDecimal("20000.00"),
            Map.of(), List.of(), OrderStatus.VALIDATED, "Test", now, now, null, List.of()
        );

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        OrderResponse response = new OrderResponse(
            "123", "customer-1", "product-1", InsuranceType.TRAVEL, SalesChannel.BRANCH,
            PaymentMethod.BANK_TRANSFER, new BigDecimal("300.00"), new BigDecimal("30000.00"),
            Map.of("basic", new BigDecimal("1000.00")), List.of("assistance"),
            OrderStatus.REJECTED, "Travel insurance", LocalDateTime.now(),
            LocalDateTime.now(), null, List.of()
        );

        // When
        String toString = response.toString();

        // Then
        assertThat(toString).contains("OrderResponse");
        assertThat(toString).contains("123");
        assertThat(toString).contains("customer-1");
        assertThat(toString).contains("TRAVEL");
        assertThat(toString).contains("REJECTED");
    }

    @Test
    void shouldCreateOrderHistoryResponse() {
        // Given
        OrderStatus status = OrderStatus.APPROVED;
        LocalDateTime timestamp = LocalDateTime.of(2025, 1, 15, 14, 30);

        // When
        OrderResponse.OrderHistoryResponse historyResponse =
            new OrderResponse.OrderHistoryResponse(status, timestamp);

        // Then
        assertThat(historyResponse.status()).isEqualTo(status);
        assertThat(historyResponse.timestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldBeEqualOrderHistoryResponseWhenSameValues() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        OrderResponse.OrderHistoryResponse history1 =
            new OrderResponse.OrderHistoryResponse(OrderStatus.PENDING, timestamp);
        OrderResponse.OrderHistoryResponse history2 =
            new OrderResponse.OrderHistoryResponse(OrderStatus.PENDING, timestamp);

        // Then
        assertThat(history1).isEqualTo(history2);
        assertThat(history1.hashCode()).isEqualTo(history2.hashCode());
    }
}
