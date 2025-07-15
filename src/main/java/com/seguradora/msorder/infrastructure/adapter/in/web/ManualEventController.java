package com.seguradora.msorder.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.ManualPaymentEventRequest;
import com.seguradora.msorder.application.dto.ManualSubscriptionEventRequest;
import com.seguradora.msorder.application.dto.PaymentEventData;
import com.seguradora.msorder.application.dto.SubscriptionEventData;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para simulação manual de eventos externos (Payment e Subscription)
 * Permite testar diferentes cenários enviando eventos manualmente
 */
@RestController
@RequestMapping("/api/v1/manual-events")
public class ManualEventController {

    private static final Logger logger = LoggerFactory.getLogger(ManualEventController.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ManualEventController(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/payment")
    public ResponseEntity<String> publishPaymentEvent(@Valid @RequestBody ManualPaymentEventRequest request) {
        try {
            logger.info("Publicando evento manual de pagamento - OrderId: {}, Status: {}",
                       request.orderId(), request.status());

            // Criar evento de pagamento em formato JSON
            PaymentEventData paymentEvent = new PaymentEventData();
            paymentEvent.setOrderId(request.orderId());
            paymentEvent.setStatus(request.status().name());
            paymentEvent.setReason(request.reason() != null ? request.reason() : "Manual test");
            paymentEvent.setEventType("PAYMENT");
            paymentEvent.setTimestamp(java.time.LocalDateTime.now().toString());

            // Converter para JSON string
            String paymentMessage = objectMapper.writeValueAsString(paymentEvent);

            // Publicar no tópico de payment
            kafkaTemplate.send("payment-events", request.orderId(), paymentMessage);

            logger.info("Evento de pagamento publicado com sucesso para OrderId: {}", request.orderId());

            return ResponseEntity.ok("Payment event published successfully for order: " + request.orderId());

        } catch (Exception e) {
            logger.error("Erro ao publicar evento manual de pagamento para OrderId: {}", request.orderId(), e);
            return ResponseEntity.internalServerError()
                .body("Error publishing payment event: " + e.getMessage());
        }
    }

    @PostMapping("/subscription")
    public ResponseEntity<String> publishSubscriptionEvent(@Valid @RequestBody ManualSubscriptionEventRequest request) {
        try {
            logger.info("Publicando evento manual de subscrição - OrderId: {}, Status: {}",
                       request.orderId(), request.status());

            // Criar evento de subscription em formato JSON
            SubscriptionEventData subscriptionEvent = new SubscriptionEventData();
            subscriptionEvent.setOrderId(request.orderId());
            subscriptionEvent.setStatus(request.status().name());
            subscriptionEvent.setReason(request.reason() != null ? request.reason() : "Manual test");
            subscriptionEvent.setEventType("SUBSCRIPTION");
            subscriptionEvent.setTimestamp(java.time.LocalDateTime.now().toString());

            // Converter para JSON string
            String subscriptionMessage = objectMapper.writeValueAsString(subscriptionEvent);

            // Publicar no tópico de subscription
            kafkaTemplate.send("subscription-events", request.orderId(), subscriptionMessage);

            logger.info("Evento de subscrição publicado com sucesso para OrderId: {}", request.orderId());

            return ResponseEntity.ok("Subscription event published successfully for order: " + request.orderId());

        } catch (Exception e) {
            logger.error("Erro ao publicar evento manual de subscrição para OrderId: {}", request.orderId(), e);
            return ResponseEntity.internalServerError()
                .body("Error publishing subscription event: " + e.getMessage());
        }
    }

    @GetMapping("/topics")
    public ResponseEntity<Object> getAvailableTopics() {
        return ResponseEntity.ok(java.util.Map.of(
            "payment_topic", "payment-events",
            "subscription_topic", "subscription-events",
            "message_format", "orderId:status:reason",
            "available_statuses", java.util.Map.of(
                "payment", java.util.List.of("APPROVED", "REJECTED"),
                "subscription", java.util.List.of("APPROVED", "REJECTED")
            ),
            "example_usage", java.util.Map.of(
                "payment", java.util.Map.of(
                    "orderId", "123e4567-e89b-12d3-a456-426614174000",
                    "status", "APPROVED",
                    "reason", "Payment processed successfully"
                ),
                "subscription", java.util.Map.of(
                    "orderId", "123e4567-e89b-12d3-a456-426614174000",
                    "status", "APPROVED",
                    "reason", "Subscription approved after analysis"
                )
            )
        ));
    }
}
