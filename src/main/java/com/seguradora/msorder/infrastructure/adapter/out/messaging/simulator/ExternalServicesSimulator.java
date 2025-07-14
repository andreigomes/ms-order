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
                PaymentEvent.PaymentStatus status = determinePaymentStatus(amount, customerId);
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
                String riskLevel = determineRiskLevel(amount, insuranceType);
                String reason = getSubscriptionReason(status);
                BigDecimal premiumAdjustment = calculatePremiumAdjustment(amount, riskLevel);

                SubscriptionEvent subscriptionEvent = new SubscriptionEvent(
                        orderId,
                        customerId,
                        insuranceType,
                        amount,
                        status,
                        reason,
                        riskLevel,
                        premiumAdjustment,
                        LocalDateTime.now()
                );

                subscriptionKafkaTemplate.send("subscription-events", orderId, subscriptionEvent);

                log.info("📋 Subscription simulation sent for order {} - Status: {} - Risk: {} (Fixed: {})",
                        orderId, status, riskLevel, properties.getFixedSubscriptionStatus() != null);

            } catch (Exception e) {
                log.error("❌ Error simulating subscription for order: {}", orderId, e);
            }
        });
    }

    private PaymentEvent.PaymentStatus determinePaymentStatus(BigDecimal amount, String customerId) {
        // Se há configuração fixa, usa ela
        if (properties.getFixedPaymentStatus() != null && !properties.getFixedPaymentStatus().isEmpty()) {
            return PaymentEvent.PaymentStatus.valueOf(properties.getFixedPaymentStatus());
        }

        // Simula lógica de aprovação de pagamento
        if (customerId.contains("BLOCKED")) {
            return PaymentEvent.PaymentStatus.REJECTED;
        }
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            return Math.random() > 0.3 ? PaymentEvent.PaymentStatus.APPROVED : PaymentEvent.PaymentStatus.REJECTED;
        }
        return Math.random() > 0.1 ? PaymentEvent.PaymentStatus.APPROVED : PaymentEvent.PaymentStatus.REJECTED;
    }

    private SubscriptionEvent.SubscriptionStatus determineSubscriptionStatus() {
        // Se há configuração fixa, usa ela
        if (properties.getFixedSubscriptionStatus() != null && !properties.getFixedSubscriptionStatus().isEmpty()) {
            return SubscriptionEvent.SubscriptionStatus.valueOf(properties.getFixedSubscriptionStatus());
        }

        // Lógica aleatória padrão
        return Math.random() > 0.2 ? SubscriptionEvent.SubscriptionStatus.APPROVED : SubscriptionEvent.SubscriptionStatus.REJECTED;
    }

    private String determineRiskLevel(BigDecimal amount, String insuranceType) {
        // Simular análise baseada no valor e tipo de seguro
        if (amount.compareTo(new BigDecimal("500000")) > 0) return "HIGH_RISK";
        if (amount.compareTo(new BigDecimal("100000")) > 0) return "REGULAR";
        if (amount.compareTo(new BigDecimal("50000")) > 0) return "PREFERENTIAL";
        return "NO_INFO";
    }

    private BigDecimal calculatePremiumAdjustment(BigDecimal amount, String riskLevel) {
        return switch (riskLevel) {
            case "HIGH_RISK" -> amount.multiply(new BigDecimal("0.15"));
            case "REGULAR" -> amount.multiply(new BigDecimal("0.08"));
            case "PREFERENTIAL" -> amount.multiply(new BigDecimal("0.05"));
            case "NO_INFO" -> amount.multiply(new BigDecimal("0.10"));
            default -> amount.multiply(new BigDecimal("0.08"));
        };
    }

    private String getPaymentReason(PaymentEvent.PaymentStatus status) {
        return switch (status) {
            case APPROVED -> "Payment processed successfully";
            case REJECTED -> "Insufficient funds or invalid payment method";
        };
    }

    private String getSubscriptionReason(SubscriptionEvent.SubscriptionStatus status) {
        return switch (status) {
            case APPROVED -> "Risk assessment passed";
            case REJECTED -> "High risk profile detected";
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
        simulatePaymentProcessing(order.getId().getValue().toString(), order.getCustomerId().getValue(), order.getAmount());
    }

    private void sendSubscriptionEvent(Order order) {
        simulateSubscriptionAnalysis(order.getId().getValue().toString(), order.getCustomerId().getValue(),
                                   order.getInsuranceType().name(), order.getAmount());
    }
}
