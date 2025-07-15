package com.seguradora.msorder.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.SubscriptionEventData;
import com.seguradora.msorder.core.usecase.coordination.EventCoordinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer responsável por receber eventos de subscrição
 */
@Component
public class SubscriptionEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionEventConsumer.class);

    private final EventCoordinationService coordinationService;
    private final ObjectMapper objectMapper;

    public SubscriptionEventConsumer(EventCoordinationService coordinationService, ObjectMapper objectMapper) {
        this.coordinationService = coordinationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "subscription-events", groupId = "order-service-subscription-group")
    public void handleSubscriptionEvent(String message) {
        try {
            logger.info("Recebido evento de subscrição: {}", message);

            SubscriptionEventData eventData = objectMapper.readValue(message, SubscriptionEventData.class);

            if ("APPROVED".equals(eventData.getStatus())) {
                coordinationService.processSubscriptionApproval(eventData.getOrderId());
            } else if ("REJECTED".equals(eventData.getStatus())) {
                coordinationService.processSubscriptionRejection(eventData.getOrderId(), eventData.getReason());
            }

        } catch (Exception e) {
            logger.error("Erro ao processar evento de subscrição: {}", message, e);
        }
    }
}
