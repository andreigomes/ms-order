package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.SubscriptionEvent;

/**
 * Caso de uso para processar eventos de subscrição
 */
public interface ProcessSubscriptionEventUseCase {

    /**
     * Processa um evento de subscrição recebido
     * Atualiza o status da ordem baseado no resultado da análise de subscrição
     */
    void processSubscriptionEvent(SubscriptionEvent subscriptionEvent);
}
