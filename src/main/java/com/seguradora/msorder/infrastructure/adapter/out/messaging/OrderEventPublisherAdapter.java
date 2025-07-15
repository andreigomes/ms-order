package com.seguradora.msorder.infrastructure.adapter.out.messaging;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de mensageria Kafka que implementa a porta de publicação de eventos
 */
@Component
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisherAdapter.class);

    private static final String ORDER_TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventPublisherAdapter(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(Order order) {
        OrderEvent event = OrderEvent.orderCreated(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_RECEIVED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderValidated(Order order) {
        OrderEvent event = OrderEvent.orderValidated(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_VALIDATED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderPending(Order order) {
        OrderEvent event = OrderEvent.orderPending(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_PENDING event for order: {}", order.getId());
    }

    @Override
    public void publishOrderRejected(Order order) {
        OrderEvent event = OrderEvent.orderRejected(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_REJECTED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderCancelled(Order order) {
        OrderEvent event = OrderEvent.orderCancelled(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_CANCELLED event for order: {}", order.getId());
    }

    @Override
    public void publishSubscriptionApproved(Order order) {
        OrderEvent event = OrderEvent.subscriptionApproved(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published SUBSCRIPTION_APPROVED event for order: {}", order.getId());
    }

    @Override
    public void publishPaymentApproved(Order order) {
        OrderEvent event = OrderEvent.paymentApproved(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published PAYMENT_APPROVED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderApproved(Order order) {
        OrderEvent event = OrderEvent.orderApproved(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getCategory(),
            order.getInsuredAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_APPROVED event for order: {}", order.getId());
    }

    private void publishEvent(OrderEvent event) {
        try {
            kafkaTemplate.send(ORDER_TOPIC, event.orderId(), event);
            logger.debug("Event published successfully: {}", event);
        } catch (Exception e) {
            logger.error("Error publishing event: {}", event, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
