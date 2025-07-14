package com.seguradora.msorder.core.domain.entity;

import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidData() {
        // Given
        CustomerId customerId = new CustomerId("CUST001");
        ProductId productId = ProductId.of("PROD001");
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        BigDecimal insuredAmount = new BigDecimal("1500.00");
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("1200.00")));
        Assistances assistances = Assistances.of(List.of("24h assistance"));
        String description = "Seguro auto para veículo modelo 2023";

        // When
        Order order = Order.create(customerId, productId, category, salesChannel, paymentMethod,
                                 totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description);

        // Then
        assertNotNull(order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(productId, order.getProductId());
        assertEquals(category, order.getCategory());
        assertEquals(salesChannel, order.getSalesChannel());
        assertEquals(paymentMethod, order.getPaymentMethod());
        assertEquals(OrderStatus.RECEIVED, order.getStatus()); // Estado inicial correto
        assertEquals(totalMonthlyPremiumAmount, order.getTotalMonthlyPremiumAmount());
        assertEquals(insuredAmount, order.getInsuredAmount());
        assertEquals(coverages, order.getCoverages());
        assertEquals(assistances, order.getAssistances());
        assertEquals(description, order.getDescription());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullCustomerId() {
        // Given
        ProductId productId = ProductId.of("PROD001");
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        BigDecimal insuredAmount = new BigDecimal("1500.00");
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("1200.00")));
        Assistances assistances = Assistances.of(List.of("24h assistance"));

        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(null, productId, category, salesChannel, paymentMethod,
                             totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNegativeInsuredAmount() {
        // Given
        CustomerId customerId = new CustomerId("CUST001");
        ProductId productId = ProductId.of("PROD001");
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        BigDecimal insuredAmount = new BigDecimal("-100.00"); // Valor negativo
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("1200.00")));
        Assistances assistances = Assistances.of(List.of("24h assistance"));

        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(customerId, productId, category, salesChannel, paymentMethod,
                             totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, "Description"));
    }

    @Test
    void shouldApproveOrderWhenStatusIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate(); // RECEIVED -> VALIDATED
        order.markAsPending(); // VALIDATED -> PENDING

        // When
        order.approve();

        // Then
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        assertNotNull(order.getFinishedAt());
    }

    @Test
    void shouldRejectOrderWhenStatusIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate(); // RECEIVED -> VALIDATED
        order.markAsPending(); // VALIDATED -> PENDING

        // When
        order.reject();

        // Then
        assertEquals(OrderStatus.REJECTED, order.getStatus());
        assertNotNull(order.getFinishedAt());
    }

    @Test
    void shouldCancelOrderWhenStatusIsReceived() {
        // Given
        Order order = createValidOrder();

        // When
        order.cancel();

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getFinishedAt());
    }

    @Test
    void shouldThrowExceptionWhenTryingToCancelApprovedOrder() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        order.approve();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.cancel());
    }

    @Test
    void shouldTransitionFromReceivedToValidated() {
        // Given
        Order order = createValidOrder();
        assertEquals(OrderStatus.RECEIVED, order.getStatus());

        // When
        order.validate();

        // Then
        assertEquals(OrderStatus.VALIDATED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldTransitionFromValidatedToPending() {
        // Given
        Order order = createValidOrder();
        order.validate();
        assertEquals(OrderStatus.VALIDATED, order.getStatus());

        // When
        order.markAsPending();

        // Then
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldNotAllowInvalidStateTransitions() {
        // Given
        Order order = createValidOrder();

        // When & Then - Não deve permitir transição direta de RECEIVED para PENDING
        assertThrows(IllegalStateException.class, () -> order.markAsPending());
    }


    private Order createValidOrder() {
        return createValidOrderWithAmount(new BigDecimal("1500.00"));
    }

    private Order createValidOrderWithAmount(BigDecimal insuredAmount) {
        CustomerId customerId = new CustomerId("CUST001");
        ProductId productId = ProductId.of("PROD001");
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("1000.00")));
        Assistances assistances = Assistances.of(List.of("24h assistance"));
        String description = "Seguro auto para teste";

        return Order.create(customerId, productId, category, salesChannel, paymentMethod,
                          totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description);
    }
}
