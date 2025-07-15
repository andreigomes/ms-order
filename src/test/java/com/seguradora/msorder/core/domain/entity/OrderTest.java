package com.seguradora.msorder.core.domain.entity;

import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        assertEquals("PENDING", order.getPaymentApproved());
        assertEquals("PENDING", order.getSubscriptionApproved());
        assertNotNull(order.getHistory());
        assertNull(order.getFinishedAt()); // Não finalizado no início
    }

    @Test
    void shouldRestoreOrderWithAllFields() {
        // Given
        OrderId id = OrderId.generate();
        CustomerId customerId = new CustomerId("CUST001");
        ProductId productId = ProductId.of("PROD001");
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        BigDecimal insuredAmount = new BigDecimal("1500.00");
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("1200.00")));
        Assistances assistances = Assistances.of(List.of("24h assistance"));
        OrderStatus status = OrderStatus.APPROVED;
        String description = "Test order";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
        LocalDateTime finishedAt = LocalDateTime.now();
        OrderHistory history = OrderHistory.empty().addEntry(null, OrderStatus.RECEIVED, "Created");
        String paymentApproved = "APPROVED";
        String subscriptionApproved = "APPROVED";

        // When
        Order order = Order.restore(id, customerId, productId, category, salesChannel, paymentMethod,
                                   totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, status,
                                   description, createdAt, updatedAt, finishedAt, history, paymentApproved, subscriptionApproved, 0L);

        // Then
        assertEquals(id, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(productId, order.getProductId());
        assertEquals(category, order.getCategory());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
        assertEquals(finishedAt, order.getFinishedAt());
        assertEquals(history, order.getHistory());
        assertEquals(paymentApproved, order.getPaymentApproved());
        assertEquals(subscriptionApproved, order.getSubscriptionApproved());
    }

    @Test
    void shouldRestoreOrderWithNullHistory() {
        // Given
        OrderId id = OrderId.generate();
        CustomerId customerId = new CustomerId("CUST001");

        // When
        Order order = Order.restore(id, customerId, ProductId.of("PROD001"), InsuranceType.AUTO,
                                   SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                                   new BigDecimal("150.00"), new BigDecimal("1500.00"),
                                   Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                                   Assistances.of(List.of("24h assistance")), OrderStatus.RECEIVED,
                                   "Test", LocalDateTime.now(), LocalDateTime.now(), null,
                                   null, "PENDING", "PENDING", 0L); // history null

        // Then
        assertNotNull(order.getHistory());
        assertTrue(order.getHistory().getEntries().isEmpty());
    }

    // Testes de validação de parâmetros
    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullCustomerId() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(null, ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullProductId() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), null, InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullCategory() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), null,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullSalesChannel() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             null, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullPaymentMethod() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, null,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullTotalMonthlyPremiumAmount() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             null, new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullInsuredAmount() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), null,
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullCoverages() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             null, Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNullAssistances() {
        // When & Then
        assertThrows(NullPointerException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             null, "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithZeroTotalMonthlyPremiumAmount() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             BigDecimal.ZERO, new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNegativeTotalMonthlyPremiumAmount() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("-100.00"), new BigDecimal("1500.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithZeroInsuredAmount() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), BigDecimal.ZERO,
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNegativeInsuredAmount() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(new CustomerId("CUST001"), ProductId.of("PROD001"), InsuranceType.AUTO,
                             SalesChannel.WEB_SITE, PaymentMethod.CREDIT_CARD,
                             new BigDecimal("150.00"), new BigDecimal("-100.00"),
                             Coverages.of(Map.of("collision", new BigDecimal("1200.00"))),
                             Assistances.of(List.of("24h assistance")), "Description"));
    }

    // Testes de transições de status
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
    void shouldThrowExceptionWhenApprovingOrderNotInPendingState() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.approve());
    }

    @Test
    void shouldMarkOrderAsPendingWhenStatusIsValidated() {
        // Given
        Order order = createValidOrder();
        order.validate();

        // When
        order.markAsPending();

        // Then
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMarkingAsPendingOrderNotInValidatedState() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.markAsPending());
    }

    @Test
    void shouldValidateOrderWhenStatusIsReceived() {
        // Given
        Order order = createValidOrder();

        // When
        order.validate();

        // Then
        assertEquals(OrderStatus.VALIDATED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenValidatingOrderNotInReceivedState() {
        // Given
        Order order = createValidOrder();
        order.validate();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.validate());
    }

    @Test
    void shouldRejectOrderWhenStatusIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();

        // When
        order.reject();

        // Then
        assertEquals(OrderStatus.REJECTED, order.getStatus());
        assertNotNull(order.getFinishedAt());
    }

    @Test
    void shouldRejectOrderWithCustomReason() {
        // Given
        Order order = createValidOrder();
        String reason = "Custom rejection reason";

        // When
        order.reject(reason);

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
    void shouldCancelOrderWithCustomReason() {
        // Given
        Order order = createValidOrder();
        String reason = "Custom cancellation reason";

        // When
        order.cancel(reason);

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

    // Testes de updateStatus
    @Test
    void shouldUpdateStatusWithoutReason() {
        // Given
        Order order = createValidOrder();

        // When
        order.updateStatus(OrderStatus.VALIDATED);

        // Then
        assertEquals(OrderStatus.VALIDATED, order.getStatus());
    }

    @Test
    void shouldUpdateStatusWithReason() {
        // Given
        Order order = createValidOrder();
        String reason = "Custom validation reason";

        // When
        order.updateStatus(OrderStatus.VALIDATED, reason);

        // Then
        assertEquals(OrderStatus.VALIDATED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> order.updateStatus(OrderStatus.APPROVED)); // RECEIVED -> APPROVED não é válido
    }

    @Test
    void shouldSetFinishedAtForFinalStates() {
        // Given
        Order order1 = createValidOrder();
        Order order2 = createValidOrder();
        Order order3 = createValidOrder();

        // When
        order1.updateStatus(OrderStatus.REJECTED, "Test rejection");
        order2.updateStatus(OrderStatus.CANCELLED, "Test cancellation");

        order3.validate();
        order3.markAsPending();
        order3.updateStatus(OrderStatus.APPROVED, "Test approval");

        // Then
        assertNotNull(order1.getFinishedAt());
        assertNotNull(order2.getFinishedAt());
        assertNotNull(order3.getFinishedAt());
    }

    // Testes de coordenação de eventos (payment e subscription)
    @Test
    void shouldApprovePaymentWhenOrderIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();

        // When
        boolean canFinalize = order.approvePayment();

        // Then
        assertEquals("APPROVED", order.getPaymentApproved());
        assertFalse(canFinalize); // subscription ainda está PENDING
    }

    @Test
    void shouldThrowExceptionWhenApprovingPaymentForOrderNotInPendingState() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.approvePayment());
    }

    @Test
    void shouldRejectPaymentWhenOrderIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        String reason = "Insufficient funds";

        // When
        order.rejectPayment(reason);

        // Then
        assertEquals("REJECTED", order.getPaymentApproved());
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingPaymentForOrderNotInPendingState() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.rejectPayment("Test reason"));
    }

    @Test
    void shouldApproveSubscriptionWhenOrderIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();

        // When
        boolean canFinalize = order.approveSubscription();

        // Then
        assertEquals("APPROVED", order.getSubscriptionApproved());
        assertFalse(canFinalize); // payment ainda está PENDING
    }

    @Test
    void shouldThrowExceptionWhenApprovingSubscriptionForOrderNotInPendingState() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.approveSubscription());
    }

    @Test
    void shouldRejectSubscriptionWhenOrderIsPending() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        String reason = "High risk profile";

        // When
        order.rejectSubscription(reason);

        // Then
        assertEquals("REJECTED", order.getSubscriptionApproved());
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingSubscriptionForOrderNotInPendingState() {
        // Given
        Order order = createValidOrder();

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.rejectSubscription("Test reason"));
    }

    @Test
    void shouldReturnTrueWhenBothPaymentAndSubscriptionAreApproved() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        order.approvePayment();
        order.approveSubscription();

        // When
        boolean canFinalize = order.canBeFinalized();

        // Then
        assertTrue(canFinalize);
    }

    @Test
    void shouldReturnFalseWhenOnlyPaymentIsApproved() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        order.approvePayment();

        // When
        boolean canFinalize = order.canBeFinalized();

        // Then
        assertFalse(canFinalize);
    }

    @Test
    void shouldReturnFalseWhenOnlySubscriptionIsApproved() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        order.approveSubscription();

        // When
        boolean canFinalize = order.canBeFinalized();

        // Then
        assertFalse(canFinalize);
    }

    @Test
    void shouldReturnTrueWhenEitherPaymentOrSubscriptionIsRejected() {
        // Given
        Order order1 = createValidOrder();
        order1.validate();
        order1.markAsPending();
        order1.rejectPayment("Payment failed");

        Order order2 = createValidOrder();
        order2.validate();
        order2.markAsPending();
        order2.rejectSubscription("Subscription denied");

        // When
        boolean hasRejection1 = order1.hasAnyRejection();
        boolean hasRejection2 = order2.hasAnyRejection();

        // Then
        assertTrue(hasRejection1);
        assertTrue(hasRejection2);
    }

    @Test
    void shouldReturnFalseWhenNoRejections() {
        // Given
        Order order = createValidOrder();

        // When
        boolean hasRejection = order.hasAnyRejection();

        // Then
        assertFalse(hasRejection);
    }

    @Test
    void shouldFinalizeApprovalWhenBothPaymentAndSubscriptionAreApproved() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        order.approvePayment();
        order.approveSubscription();

        // When
        order.finalizeApproval();

        // Then
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        assertNotNull(order.getFinishedAt());
    }

    @Test
    void shouldThrowExceptionWhenFinalizingWithoutBothApprovals() {
        // Given
        Order order = createValidOrder();
        order.validate();
        order.markAsPending();
        order.approvePayment(); // subscription ainda PENDING

        // When & Then
        assertThrows(IllegalStateException.class, () -> order.finalizeApproval());
    }

    // Testes de equals e hashCode
    @Test
    void shouldBeEqualWhenSameId() {
        // Given
        Order order1 = createValidOrder();
        Order order2 = Order.restore(order1.getId(), order1.getCustomerId(), order1.getProductId(),
                                    order1.getCategory(), order1.getSalesChannel(), order1.getPaymentMethod(),
                                    order1.getTotalMonthlyPremiumAmount(), order1.getInsuredAmount(),
                                    order1.getCoverages(), order1.getAssistances(), order1.getStatus(),
                                    order1.getDescription(), order1.getCreatedAt(), order1.getUpdatedAt(),
                                    order1.getFinishedAt(), order1.getHistory(), order1.getPaymentApproved(),
                                    order1.getSubscriptionApproved(), 0L);

        // Then
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        // Given
        Order order1 = createValidOrder();
        Order order2 = createValidOrder();

        // Then
        assertNotEquals(order1, order2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Given
        Order order = createValidOrder();

        // Then
        assertNotEquals(order, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        Order order = createValidOrder();
        String otherObject = "test";

        // Then
        assertNotEquals(order, otherObject);
    }

    @Test
    void shouldBeEqualToItself() {
        // Given
        Order order = createValidOrder();

        // Then
        assertEquals(order, order);
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
