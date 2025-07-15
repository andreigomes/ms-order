package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * Testes para verificar o contrato da interface OrderEventPublisherPort
 */
class OrderEventPublisherPortTest {

    @Test
    void shouldPublishOrderCreatedEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderCreated(any(Order.class));

        // When
        port.publishOrderCreated(order);

        // Then
        verify(port).publishOrderCreated(order);
    }

    @Test
    void shouldPublishOrderValidatedEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderValidated(any(Order.class));

        // When
        port.publishOrderValidated(order);

        // Then
        verify(port).publishOrderValidated(order);
    }

    @Test
    void shouldPublishOrderPendingEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderPending(any(Order.class));

        // When
        port.publishOrderPending(order);

        // Then
        verify(port).publishOrderPending(order);
    }

    @Test
    void shouldPublishOrderApprovedEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderApproved(any(Order.class));

        // When
        port.publishOrderApproved(order);

        // Then
        verify(port).publishOrderApproved(order);
    }

    @Test
    void shouldPublishOrderRejectedEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderRejected(any(Order.class));

        // When
        port.publishOrderRejected(order);

        // Then
        verify(port).publishOrderRejected(order);
    }

    @Test
    void shouldPublishOrderCancelledEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderCancelled(any(Order.class));

        // When
        port.publishOrderCancelled(order);

        // Then
        verify(port).publishOrderCancelled(order);
    }

    @Test
    void shouldPublishPaymentApprovedEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishPaymentApproved(any(Order.class));

        // When
        port.publishPaymentApproved(order);

        // Then
        verify(port).publishPaymentApproved(order);
    }

    @Test
    void shouldPublishSubscriptionApprovedEvent() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishSubscriptionApproved(any(Order.class));

        // When
        port.publishSubscriptionApproved(order);

        // Then
        verify(port).publishSubscriptionApproved(order);
    }

    @Test
    void shouldPublishAllEventsForCompleteOrderFlow() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderCreated(any(Order.class));
        doNothing().when(port).publishOrderValidated(any(Order.class));
        doNothing().when(port).publishOrderPending(any(Order.class));
        doNothing().when(port).publishOrderApproved(any(Order.class));
        doNothing().when(port).publishPaymentApproved(any(Order.class));
        doNothing().when(port).publishSubscriptionApproved(any(Order.class));

        // When - Simula um fluxo completo de pedido
        port.publishOrderCreated(order);
        port.publishOrderValidated(order);
        port.publishOrderPending(order);
        port.publishOrderApproved(order);
        port.publishPaymentApproved(order);
        port.publishSubscriptionApproved(order);

        // Then
        verify(port).publishOrderCreated(order);
        verify(port).publishOrderValidated(order);
        verify(port).publishOrderPending(order);
        verify(port).publishOrderApproved(order);
        verify(port).publishPaymentApproved(order);
        verify(port).publishSubscriptionApproved(order);
    }

    @Test
    void shouldPublishRejectionFlow() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderCreated(any(Order.class));
        doNothing().when(port).publishOrderValidated(any(Order.class));
        doNothing().when(port).publishOrderRejected(any(Order.class));

        // When - Simula um fluxo de rejeição
        port.publishOrderCreated(order);
        port.publishOrderValidated(order);
        port.publishOrderRejected(order);

        // Then
        verify(port).publishOrderCreated(order);
        verify(port).publishOrderValidated(order);
        verify(port).publishOrderRejected(order);
    }

    @Test
    void shouldPublishCancellationFlow() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);
        Order order = createTestOrder();

        doNothing().when(port).publishOrderCreated(any(Order.class));
        doNothing().when(port).publishOrderCancelled(any(Order.class));

        // When - Simula um fluxo de cancelamento
        port.publishOrderCreated(order);
        port.publishOrderCancelled(order);

        // Then
        verify(port).publishOrderCreated(order);
        verify(port).publishOrderCancelled(order);
    }

    @Test
    void shouldVerifyMethodSignatures() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);

        // When & Then - Verifica que todos os métodos existem na interface
        assertThat(port).isNotNull();

        java.lang.reflect.Method[] methods = OrderEventPublisherPort.class.getDeclaredMethods();
        assertThat(methods).hasSize(8);

        // Verifica os nomes dos métodos
        String[] expectedMethods = {
            "publishOrderCreated",
            "publishOrderValidated",
            "publishOrderPending",
            "publishOrderApproved",
            "publishOrderRejected",
            "publishOrderCancelled",
            "publishPaymentApproved",
            "publishSubscriptionApproved"
        };

        for (String methodName : expectedMethods) {
            boolean methodExists = false;
            for (java.lang.reflect.Method method : methods) {
                if (method.getName().equals(methodName)) {
                    methodExists = true;
                    assertThat(method.getParameterCount()).isEqualTo(1);
                    assertThat(method.getParameterTypes()[0]).isEqualTo(Order.class);
                    assertThat(method.getReturnType()).isEqualTo(void.class);
                    break;
                }
            }
            assertThat(methodExists).as("Method %s should exist", methodName).isTrue();
        }
    }

    @Test
    void shouldHandleDifferentOrderTypes() {
        // Given
        OrderEventPublisherPort port = Mockito.mock(OrderEventPublisherPort.class);

        // Testa com diferentes tipos de seguro
        for (InsuranceType type : InsuranceType.values()) {
            Order order = Order.create(
                new CustomerId("CUST-" + type.name()),
                ProductId.of("PROD-" + type.name()),
                type,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("100.00"),
                new BigDecimal("10000.00"),
                Coverages.of(Map.of("basic", new BigDecimal("8000.00"))),
                Assistances.of(List.of("basic assistance")),
                "Test order for " + type.name()
            );

            doNothing().when(port).publishOrderCreated(any(Order.class));

            // When
            port.publishOrderCreated(order);

            // Then
            verify(port).publishOrderCreated(order);
        }
    }

    private Order createTestOrder() {
        return Order.create(
            new CustomerId("TEST_CUSTOMER"),
            ProductId.of("TEST_PRODUCT"),
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("150.00"),
            new BigDecimal("50000.00"),
            Coverages.of(Map.of("collision", new BigDecimal("40000.00"))),
            Assistances.of(List.of("24h assistance")),
            "Test order description"
        );
    }
}
