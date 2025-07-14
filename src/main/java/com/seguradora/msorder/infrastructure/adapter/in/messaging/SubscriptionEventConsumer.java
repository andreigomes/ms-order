package com.seguradora.msorder.infrastructure.adapter.in.messaging;

import com.seguradora.msorder.core.port.in.ProcessSubscriptionEventUseCase;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.SubscriptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de subscrição
 * Processa eventos vindos do serviço de subscrição/underwriting
 */
@Component
public class SubscriptionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionEventConsumer.class);

    private final ProcessSubscriptionEventUseCase processSubscriptionEventUseCase;

    public SubscriptionEventConsumer(ProcessSubscriptionEventUseCase processSubscriptionEventUseCase) {
        this.processSubscriptionEventUseCase = processSubscriptionEventUseCase;
    }

    @KafkaListener(
        topics = "subscription-events",
        groupId = "order-service-subscription-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionEvent(
            @Payload SubscriptionEvent subscriptionEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("🔔 Received subscription event: {} from topic: {}, partition: {}, offset: {}",
                    subscriptionEvent, topic, partition, offset);

            processSubscriptionEventUseCase.processSubscriptionEvent(subscriptionEvent);

            // Confirma o processamento da mensagem
            acknowledgment.acknowledge();

            log.info("✅ Subscription event processed successfully for order: {}", subscriptionEvent.orderId());

        } catch (Exception e) {
            log.error("❌ Error processing subscription event for order: {} - Error: {}",
                    subscriptionEvent.orderId(), e.getMessage(), e);
            // Não confirma a mensagem em caso de erro para tentar reprocessar
            throw e;
        }
    }
}
