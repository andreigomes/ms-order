package com.seguradora.msorder.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.PaymentEventData;
import com.seguradora.msorder.core.usecase.coordination.EventCoordinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer responsável por receber eventos de pagamento
 */
@Component
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final EventCoordinationService coordinationService;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(EventCoordinationService coordinationService, ObjectMapper objectMapper) {
        this.coordinationService = coordinationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-service-payment-group")
    public void handlePaymentEvent(String message) {
        try {
            logger.info("Recebido evento de pagamento: {}", message);

            PaymentEventData eventData = objectMapper.readValue(message, PaymentEventData.class);

            if ("APPROVED".equals(eventData.getStatus())) {
                coordinationService.processPaymentApproval(eventData.getOrderId());
            } else if ("REJECTED".equals(eventData.getStatus())) {
                coordinationService.processPaymentRejection(eventData.getOrderId(), eventData.getReason());
            }

        } catch (Exception e) {
            logger.error("Erro ao processar evento de pagamento: {}", message, e);
        }
    }
}
