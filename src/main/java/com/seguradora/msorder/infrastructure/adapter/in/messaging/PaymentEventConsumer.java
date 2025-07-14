package com.seguradora.msorder.infrastructure.adapter.in.messaging;

import com.seguradora.msorder.core.port.in.ProcessPaymentEventUseCase;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de pagamento
 * Processa eventos vindos do serviço de pagamentos
 */
@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final ProcessPaymentEventUseCase processPaymentEventUseCase;

    public PaymentEventConsumer(ProcessPaymentEventUseCase processPaymentEventUseCase) {
        this.processPaymentEventUseCase = processPaymentEventUseCase;
    }

    @KafkaListener(
        topics = "payment-events",
        groupId = "order-service-payment-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvent(
            @Payload PaymentEvent paymentEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("🔔 Received payment event: {} from topic: {}, partition: {}, offset: {}",
                    paymentEvent, topic, partition, offset);

            processPaymentEventUseCase.processPaymentEvent(paymentEvent);

            // Confirma o processamento da mensagem
            acknowledgment.acknowledge();

            log.info("✅ Payment event processed successfully for order: {}", paymentEvent.orderId());

        } catch (Exception e) {
            log.error("❌ Error processing payment event for order: {} - Error: {}",
                    paymentEvent.orderId(), e.getMessage(), e);
            // Não confirma a mensagem em caso de erro para tentar reprocessar
            throw e;
        }
    }
}
