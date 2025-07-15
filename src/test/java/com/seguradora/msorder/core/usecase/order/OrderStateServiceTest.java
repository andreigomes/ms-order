package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.UpdateOrderStatusUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStateServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private OrderEventPublisherPort orderEventPublisherPort;

    private UpdateOrderStatusService updateOrderStatusService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        updateOrderStatusService = new UpdateOrderStatusService(orderRepositoryPort, orderEventPublisherPort);

        testOrder = Order.create(
                new CustomerId("123"),
                ProductId.of("PROD001"),
                InsuranceType.AUTO,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("500.00"),
                new BigDecimal("50000.00"),
                Coverages.of(Map.of("collision", new BigDecimal("40000.00"))),
                Assistances.of(List.of("24h assistance")),
                "Test order"
        );
    }

    @Test
    void shouldApproveOrder() {
        // Given
        Order pendingOrder = Order.create(
                new CustomerId("123"),
                ProductId.of("PROD001"),
                InsuranceType.AUTO,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("500.00"),
                new BigDecimal("50000.00"),
                Coverages.of(Map.of("collision", new BigDecimal("40000.00"))),
                Assistances.of(List.of("24h assistance")),
                "Test order"
        );
        // Seguir sequência correta de estados: RECEIVED → VALIDATED → PENDING
        pendingOrder.validate();
        pendingOrder.markAsPending();

        when(orderRepositoryPort.findById(any(OrderId.class))).thenReturn(java.util.Optional.of(pendingOrder));
        when(orderRepositoryPort.save(any(Order.class))).thenReturn(pendingOrder);

        // When
        Order result = updateOrderStatusService.approveOrder(
                new UpdateOrderStatusUseCase.ApproveOrderCommand(pendingOrder.getId())
        );

        // Then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.APPROVED);
        verify(orderRepositoryPort).save(any(Order.class));
        verify(orderEventPublisherPort).publishOrderApproved(any(Order.class));
    }

    @Test
    void shouldRejectOrder() {
        // Given
        Order pendingOrder = Order.create(
                new CustomerId("123"),
                ProductId.of("PROD001"),
                InsuranceType.AUTO,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("500.00"),
                new BigDecimal("50000.00"),
                Coverages.of(Map.of("collision", new BigDecimal("40000.00"))),
                Assistances.of(List.of("24h assistance")),
                "Test order"
        );
        // Seguir sequência correta de estados: RECEIVED → VALIDATED → PENDING
        pendingOrder.validate();
        pendingOrder.markAsPending();

        when(orderRepositoryPort.findById(any(OrderId.class))).thenReturn(java.util.Optional.of(pendingOrder));
        when(orderRepositoryPort.save(any(Order.class))).thenReturn(pendingOrder);

        // When
        Order result = updateOrderStatusService.rejectOrder(
                new UpdateOrderStatusUseCase.RejectOrderCommand(pendingOrder.getId())
        );

        // Then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.REJECTED);
        verify(orderRepositoryPort).save(any(Order.class));
        verify(orderEventPublisherPort).publishOrderRejected(any(Order.class));
    }

    @Test
    void shouldCancelOrder() {
        // Given
        when(orderRepositoryPort.findById(any(OrderId.class))).thenReturn(java.util.Optional.of(testOrder));
        when(orderRepositoryPort.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order result = updateOrderStatusService.cancelOrder(
                new UpdateOrderStatusUseCase.CancelOrderCommand(testOrder.getId())
        );

        // Then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepositoryPort).save(any(Order.class));
        verify(orderEventPublisherPort).publishOrderCancelled(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        OrderId validOrderId = OrderId.of("550e8400-e29b-41d4-a716-446655440000"); // UUID válido
        when(orderRepositoryPort.findById(any(OrderId.class))).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> updateOrderStatusService.approveOrder(
                new UpdateOrderStatusUseCase.ApproveOrderCommand(validOrderId)
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }
}
