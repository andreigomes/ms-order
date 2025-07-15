package com.seguradora.msorder.infrastructure.adapter.out.messaging;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.event.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherAdapterTest {

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @InjectMocks
    private OrderEventPublisherAdapter orderEventPublisherAdapter;

    private Order mockOrder;
    private CustomerId customerId;

    @BeforeEach
    void setUp() {
        customerId = new CustomerId("customer-123");

        // Criar um Order que será usado em todos os testes
        mockOrder = Order.create(
            customerId,
            ProductId.of("product-456"),
            InsuranceType.AUTO,
            SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("100000.00"),
            Coverages.of(Map.of("Collision", new BigDecimal("50000"))),
            Assistances.of(List.of("24h Roadside Assistance")),
            "Test order description"
        );
    }

    @Test
    void shouldPublishOrderCreatedEvent() {
        // When
        orderEventPublisherAdapter.publishOrderCreated(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("ORDER_RECEIVED");
    }

    @Test
    void shouldPublishOrderValidatedEvent() {
        // When
        orderEventPublisherAdapter.publishOrderValidated(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("ORDER_VALIDATED");
    }

    @Test
    void shouldPublishOrderPendingEvent() {
        // When
        orderEventPublisherAdapter.publishOrderPending(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("ORDER_PENDING");
    }

    @Test
    void shouldPublishOrderRejectedEvent() {
        // When
        orderEventPublisherAdapter.publishOrderRejected(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("ORDER_REJECTED");
    }

    @Test
    void shouldPublishOrderCancelledEvent() {
        // When
        orderEventPublisherAdapter.publishOrderCancelled(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("ORDER_CANCELLED");
    }

    @Test
    void shouldPublishSubscriptionApprovedEvent() {
        // When
        orderEventPublisherAdapter.publishSubscriptionApproved(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("SUBSCRIPTION_APPROVED");
    }

    @Test
    void shouldPublishPaymentApprovedEvent() {
        // When
        orderEventPublisherAdapter.publishPaymentApproved(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("PAYMENT_APPROVED");
    }

    @Test
    void shouldPublishOrderApprovedEvent() {
        // When
        orderEventPublisherAdapter.publishOrderApproved(mockOrder);

        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), eventCaptor.capture());

        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.orderId()).isEqualTo(mockOrder.getId().getValue().toString());
        assertThat(capturedEvent.customerId()).isEqualTo(customerId.getValue());
        assertThat(capturedEvent.eventType()).isEqualTo("ORDER_APPROVED");
    }

    @Test
    void shouldThrowExceptionWhenKafkaFails() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEvent.class)))
            .thenThrow(new RuntimeException("Kafka connection failed"));

        // When & Then
        assertThatThrownBy(() -> orderEventPublisherAdapter.publishOrderCreated(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        verify(kafkaTemplate).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), any(OrderEvent.class));
    }

    @Test
    void shouldHandleKafkaExceptionForAllEventTypes() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEvent.class)))
            .thenThrow(new RuntimeException("Network error"));

        // When & Then - Testando diferentes tipos de evento
        assertThatThrownBy(() -> orderEventPublisherAdapter.publishOrderValidated(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        assertThatThrownBy(() -> orderEventPublisherAdapter.publishOrderPending(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        assertThatThrownBy(() -> orderEventPublisherAdapter.publishOrderRejected(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        assertThatThrownBy(() -> orderEventPublisherAdapter.publishOrderCancelled(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        assertThatThrownBy(() -> orderEventPublisherAdapter.publishSubscriptionApproved(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        assertThatThrownBy(() -> orderEventPublisherAdapter.publishPaymentApproved(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        assertThatThrownBy(() -> orderEventPublisherAdapter.publishOrderApproved(mockOrder))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to publish event");

        // Verifica se o KafkaTemplate foi chamado para todos os eventos
        verify(kafkaTemplate, times(7)).send(eq("order-events"), eq(mockOrder.getId().getValue().toString()), any(OrderEvent.class));
    }
}
