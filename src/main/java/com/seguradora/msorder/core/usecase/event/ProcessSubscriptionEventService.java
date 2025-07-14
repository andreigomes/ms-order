package com.seguradora.msorder.core.usecase.event;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.port.in.ProcessSubscriptionEventUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.SubscriptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por processar eventos de subscrição/underwriting
 */
@Service
@Transactional
public class ProcessSubscriptionEventService implements ProcessSubscriptionEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessSubscriptionEventService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public ProcessSubscriptionEventService(OrderRepositoryPort orderRepository,
                                         OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processSubscriptionEvent(SubscriptionEvent subscriptionEvent) {
        log.info("📋 Processing subscription event for order: {} with status: {}",
                subscriptionEvent.orderId(), subscriptionEvent.status());

        try {
            OrderId orderId = OrderId.of(subscriptionEvent.orderId());
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Processa baseado no status da subscrição
            switch (subscriptionEvent.status()) {
                case APPROVED -> handleSubscriptionApproved(order, subscriptionEvent);
                case REJECTED -> handleSubscriptionRejected(order, subscriptionEvent);
            }

        } catch (Exception e) {
            log.error("❌ Error processing subscription event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process subscription event", e);
        }
    }

    private void handleSubscriptionApproved(Order order, SubscriptionEvent subscriptionEvent) {
        log.info("✅ Subscription approved for order: {} - Reason: {}",
                order.getId(), subscriptionEvent.reason());

        // Se ordem está PENDING, verifica se pode aprovar final
        if (order.getStatus() == OrderStatus.PENDING) {
            // TODO: Implementar lógica para verificar se pagamento também foi aprovado
            // Por enquanto, considera que se chegou aqui é porque ambos foram aprovados
            order.approve(); // PENDING -> APPROVED
            orderRepository.save(order);

            // Publica evento de ordem aprovada
            eventPublisher.publishSubscriptionApproved(order);

            log.info("🎉 Order {} fully approved - ready for policy issuance", order.getId());
        } else {
            log.warn("⚠️ Subscription approved but order {} is not in PENDING state: {}", order.getId(), order.getStatus());
        }
    }

    private void handleSubscriptionRejected(Order order, SubscriptionEvent subscriptionEvent) {
        log.info("❌ Subscription rejected for order: {} - Reason: {}",
                order.getId(), subscriptionEvent.reason());

        // Se ordem está PENDING, rejeita por falha na subscrição
        if (order.getStatus() == OrderStatus.PENDING) {
            order.reject(); // PENDING -> REJECTED
            orderRepository.save(order);

            // Publica evento de subscrição rejeitada
            eventPublisher.publishOrderRejected(order);

            log.info("📤 Order {} rejected due to subscription failure", order.getId());
        }
    }
}
