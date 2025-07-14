package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.PaymentEvent;

/**
 * Caso de uso para processar eventos de pagamento
 */
public interface ProcessPaymentEventUseCase {

    /**
     * Processa um evento de pagamento recebido
     * Atualiza o status da ordem baseado no resultado do pagamento
     */
    void processPaymentEvent(PaymentEvent paymentEvent);
}
