package com.seguradora.msorder.infrastructure.adapter.out.messaging.simulator;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.PaymentEvent;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.SubscriptionEvent;
import com.seguradora.msorder.infrastructure.config.ExternalServicesSimulatorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Simulador de serviços externos de pagamento e subscrição
 * Simula o comportamento dos serviços de pagamento e underwriting
 */
@Component
@ConditionalOnProperty(prefix = "app.simulator", name = "enabled", havingValue = "true")
public class ExternalServicesSimulator implements ExternalServicesSimulatorInterface {

    private static final Logger log = LoggerFactory.getLogger(ExternalServicesSimulator.class);

    private final KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate;
    private final KafkaTemplate<String, SubscriptionEvent> subscriptionKafkaTemplate;
    private final ExternalServicesSimulatorProperties properties;

    public ExternalServicesSimulator(KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate,
                                   KafkaTemplate<String, SubscriptionEvent> subscriptionKafkaTemplate,
                                   ExternalServicesSimulatorProperties properties) {
        this.paymentKafkaTemplate = paymentKafkaTemplate;
        this.subscriptionKafkaTemplate = subscriptionKafkaTemplate;
        this.properties = properties;
        log.info("🔧 ExternalServicesSimulator enabled with fixed statuses - Payment: {}, Subscription: {}",
                properties.getFixedPaymentStatus(), properties.getFixedSubscriptionStatus());
    }

    /**
     * Simula o processamento de pagamento com base no valor da apólice
     */
    public void simulatePaymentProcessing(String orderId, String customerId, BigDecimal amount) {
        CompletableFuture.delayedExecutor(properties.getPaymentDelaySeconds(), TimeUnit.SECONDS).execute(() -> {
            try {
                PaymentEvent.PaymentStatus status = determinePaymentStatus();
                String transactionId = UUID.randomUUID().toString();
                String reason = getPaymentReason(status);

                PaymentEvent paymentEvent = new PaymentEvent(
                        orderId,
                        customerId,
                        amount,
                        "CREDIT_CARD",
                        status,
                        transactionId,
                        reason,
                        LocalDateTime.now()
                );

                paymentKafkaTemplate.send("payment-events", orderId, paymentEvent);

                log.info("🏦 Payment simulation sent for order {} - Status: {} - Transaction: {} (Fixed: {})",
                        orderId, status, transactionId, properties.getFixedPaymentStatus() != null);

            } catch (Exception e) {
                log.error("❌ Error simulating payment for order: {}", orderId, e);
            }
        });
    }

    /**
     * Simula a análise de subscrição/underwriting
     */
    public void simulateSubscriptionAnalysis(String orderId, String customerId, String insuranceType, BigDecimal amount) {
        CompletableFuture.delayedExecutor(properties.getSubscriptionDelaySeconds(), TimeUnit.SECONDS).execute(() -> {
            try {
                SubscriptionEvent.SubscriptionStatus status = determineSubscriptionStatus();
                String reason = getSubscriptionReason(status);

                SubscriptionEvent subscriptionEvent = new SubscriptionEvent(
                        orderId,
                        customerId,
                        insuranceType,
                        amount,
                        status,
                        reason,
                        null, // riskLevel - já determinado pela análise de fraudes
                        null, // premiumAdjustment - removido conforme solicitado
                        LocalDateTime.now()
                );

                subscriptionKafkaTemplate.send("subscription-events", orderId, subscriptionEvent);

                log.info("📋 Subscription simulation sent for order {} - Status: {} (Fixed: {})",
                        orderId, status, properties.getFixedSubscriptionStatus() != null);

            } catch (Exception e) {
                log.error("❌ Error simulating subscription for order: {}", orderId, e);
            }
        });
    }

    @Override
    public void triggerExternalServices(Order order) {
        log.info("🚀 Triggering external services simulation for order: {}", order.getId().getValue());

        // Simular processamento de pagamento
        simulatePaymentProcessing(
                order.getId().getValue().toString(),
                order.getCustomerId().getValue(),
                order.getInsuredAmount()
        );

        // Simular análise de subscrição
        simulateSubscriptionAnalysis(
                order.getId().getValue().toString(),
                order.getCustomerId().getValue(),
                order.getCategory().name(),
                order.getInsuredAmount()
        );
    }

    private PaymentEvent.PaymentStatus determinePaymentStatus() {
        if (properties.getFixedPaymentStatus() != null) {
            return PaymentEvent.PaymentStatus.valueOf(properties.getFixedPaymentStatus());
        }

        // Simulação simples: 90% aprovado, 10% rejeitado
        return Math.random() < 0.9 ? PaymentEvent.PaymentStatus.APPROVED : PaymentEvent.PaymentStatus.REJECTED;
    }

    private SubscriptionEvent.SubscriptionStatus determineSubscriptionStatus() {
        if (properties.getFixedSubscriptionStatus() != null) {
            return SubscriptionEvent.SubscriptionStatus.valueOf(properties.getFixedSubscriptionStatus());
        }

        // Simulação simples: 85% aprovado, 15% rejeitado
        return Math.random() < 0.85 ? SubscriptionEvent.SubscriptionStatus.APPROVED : SubscriptionEvent.SubscriptionStatus.REJECTED;
    }

    private String getPaymentReason(PaymentEvent.PaymentStatus status) {
        return switch (status) {
            case APPROVED -> "Payment processed successfully";
            case REJECTED -> "Insufficient funds or invalid payment method";
        };
    }

    private String getSubscriptionReason(SubscriptionEvent.SubscriptionStatus status) {
        return switch (status) {
            case APPROVED -> "Risk analysis approved";
            case REJECTED -> "Risk too high for coverage";
        };
    }

    /**
     * Simula processamento de pagamento e subscrição para testes
     */
    public void simulatePaymentAndSubscriptionProcessing(Order order) {
        // Simula processamento assíncrono com delay
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000); // Simula tempo de processamento
                sendPaymentEvent(order);
                Thread.sleep(1000);
                sendSubscriptionEvent(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void sendPaymentEvent(Order order) {
        simulatePaymentProcessing(order.getId().getValue().toString(), order.getCustomerId().getValue(), order.getInsuredAmount());
    }

    private void sendSubscriptionEvent(Order order) {
        simulateSubscriptionAnalysis(order.getId().getValue().toString(), order.getCustomerId().getValue(),
                                   order.getCategory().name(), order.getInsuredAmount());
    }
}
