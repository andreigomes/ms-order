package com.seguradora.msorder.integration.config;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuração de teste para mockar o Kafka
 */
@TestConfiguration
public class TestKafkaConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestKafkaConfig.class);

    @Bean
    @Primary
    public OrderEventPublisherPort testOrderEventPublisher() {
        return new OrderEventPublisherPort() {
            @Override
            public void publishOrderCreated(Order order) {
                logger.info("Mock: Publishing ORDER_CREATED event for order: {}", order.getId());
            }

            @Override
            public void publishOrderValidated(Order order) {
                logger.info("Mock: Publishing ORDER_VALIDATED event for order: {}", order.getId());
            }

            @Override
            public void publishOrderPending(Order order) {
                logger.info("Mock: Publishing ORDER_PENDING event for order: {}", order.getId());
            }

            @Override
            public void publishOrderApproved(Order order) {
                logger.info("Mock: Publishing ORDER_APPROVED event for order: {}", order.getId());
            }

            @Override
            public void publishOrderRejected(Order order) {
                logger.info("Mock: Publishing ORDER_REJECTED event for order: {}", order.getId());
            }

            @Override
            public void publishOrderRejected(Order order, String reason) {
                logger.info("Mock: Publishing ORDER_REJECTED event for order: {} with reason: {}", order.getId(), reason);
            }

            @Override
            public void publishOrderCancelled(Order order) {
                logger.info("Mock: Publishing ORDER_CANCELLED event for order: {}", order.getId());
            }

            @Override
            public void publishOrderCompleted(Order order) {
                logger.info("Mock: Publishing ORDER_COMPLETED event for order: {}", order.getId());
            }

            @Override
            public void publishOrderPendingAnalysis(Order order, String riskLevel) {
                logger.info("Mock: Publishing ORDER_PENDING_ANALYSIS event for order: {} with risk level: {}", order.getId(), riskLevel);
            }

            @Override
            public void publishPaymentProcessed(Order order, String transactionId) {
                logger.info("Mock: Publishing PAYMENT_PROCESSED event for order: {} with transaction: {}", order.getId(), transactionId);
            }

            @Override
            public void publishPaymentRejected(Order order, String reason) {
                logger.info("Mock: Publishing PAYMENT_REJECTED event for order: {} with reason: {}", order.getId(), reason);
            }

            @Override
            public void publishSubscriptionRejected(Order order, String reason) {
                logger.info("Mock: Publishing SUBSCRIPTION_REJECTED event for order: {} with reason: {}", order.getId(), reason);
            }

            @Override
            public void publishAdditionalInfoRequired(Order order, String reason) {
                logger.info("Mock: Publishing ADDITIONAL_INFO_REQUIRED event for order: {} with reason: {}", order.getId(), reason);
            }
        };
    }
}
