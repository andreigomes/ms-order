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
            order.getInsuranceType(),
            order.getAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_CREATED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderValidated(Order order) {
        OrderEvent event = OrderEvent.orderValidated(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
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
            order.getInsuranceType(),
            order.getAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_PENDING event for order: {}", order.getId());
    }

    @Override
    public void publishOrderApproved(Order order) {
        OrderEvent event = OrderEvent.orderApproved(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_APPROVED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderRejected(Order order) {
        publishOrderRejected(order, "Order rejected");
    }

    @Override
    public void publishOrderRejected(Order order, String reason) {
        OrderEvent event = OrderEvent.orderRejected(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            reason
        );
        publishEvent(event);
        logger.info("Published ORDER_REJECTED event for order: {} - Reason: {}", order.getId(), reason);
    }

    @Override
    public void publishOrderCancelled(Order order) {
        OrderEvent event = OrderEvent.orderCancelled(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_CANCELLED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderCompleted(Order order) {
        OrderEvent event = OrderEvent.orderCompleted(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            order.getDescription()
        );
        publishEvent(event);
        logger.info("Published ORDER_COMPLETED event for order: {}", order.getId());
    }

    @Override
    public void publishOrderPendingAnalysis(Order order, String riskLevel) {
        OrderEvent event = OrderEvent.orderPendingAnalysis(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            "Pending analysis: " + riskLevel
        );
        publishEvent(event);
        logger.info("Published ORDER_PENDING_ANALYSIS event for order: {} - Risk Level: {}", order.getId(), riskLevel);
    }

    // Novos métodos para eventos de pagamento
    @Override
    public void publishPaymentProcessed(Order order, String transactionId) {
        OrderEvent event = OrderEvent.paymentProcessed(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            "Payment processed - Transaction: " + transactionId
        );
        publishEvent(event);
        logger.info("Published PAYMENT_PROCESSED event for order: {} - Transaction: {}", order.getId(), transactionId);
    }

    @Override
    public void publishPaymentRejected(Order order, String reason) {
        OrderEvent event = OrderEvent.paymentRejected(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            "Payment rejected: " + reason
        );
        publishEvent(event);
        logger.info("Published PAYMENT_REJECTED event for order: {} - Reason: {}", order.getId(), reason);
    }

    // Novos métodos para eventos de subscrição
    @Override
    public void publishSubscriptionRejected(Order order, String reason) {
        OrderEvent event = OrderEvent.subscriptionRejected(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            "Subscription rejected: " + reason
        );
        publishEvent(event);
        logger.info("Published SUBSCRIPTION_REJECTED event for order: {} - Reason: {}", order.getId(), reason);
    }

    @Override
    public void publishAdditionalInfoRequired(Order order, String reason) {
        OrderEvent event = OrderEvent.additionalInfoRequired(
            order.getId().toString(),
            order.getCustomerId().toString(),
            order.getInsuranceType(),
            order.getAmount(),
            "Additional information required: " + reason
        );
        publishEvent(event);
        logger.info("Published ADDITIONAL_INFO_REQUIRED event for order: {} - Info: {}", order.getId(), reason);
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
