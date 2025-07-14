package com.seguradora.msorder.core.usecase.event;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.port.in.ProcessPaymentEventUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.in.messaging.event.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por processar eventos de pagamento
 */
@Service
@Transactional
public class ProcessPaymentEventService implements ProcessPaymentEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentEventService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public ProcessPaymentEventService(OrderRepositoryPort orderRepository,
                                    OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processPaymentEvent(PaymentEvent paymentEvent) {
        log.info("💳 Processing payment event for order: {} with status: {}",
                paymentEvent.orderId(), paymentEvent.status());

        try {
            OrderId orderId = OrderId.of(paymentEvent.orderId());
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Processa baseado no status do pagamento
            switch (paymentEvent.status()) {
                case APPROVED -> handlePaymentApproved(order, paymentEvent);
                case REJECTED -> handlePaymentRejected(order, paymentEvent);
            }

        } catch (Exception e) {
            log.error("❌ Error processing payment event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process payment event", e);
        }
    }

    private void handlePaymentApproved(Order order, PaymentEvent paymentEvent) {
        log.info("✅ Payment approved for order: {}", order.getId());

        // Se ordem está PENDING, marca como processando pagamento
        if (order.getStatus() == OrderStatus.PENDING) {
            // Verifica se também há aprovação de subscrição para aprovar final
            // Por enquanto, só registra que pagamento foi aprovado
            // A aprovação final acontece quando ambos (pagamento E subscrição) estão OK
            log.info("💳 Payment approved for order {}, waiting for subscription approval", order.getId());

            // Publica evento de pagamento processado mas mantém ordem PENDING
            eventPublisher.publishPaymentProcessed(order, paymentEvent.transactionId());
        } else {
            log.warn("⚠️ Payment approved but order {} is not in PENDING state: {}", order.getId(), order.getStatus());
        }
    }

    private void handlePaymentRejected(Order order, PaymentEvent paymentEvent) {
        log.info("❌ Payment rejected for order: {}", order.getId());

        // Se ordem está PENDING, rejeita por falha no pagamento
        if (order.getStatus() == OrderStatus.PENDING) {
            order.reject(); // PENDING -> REJECTED
            orderRepository.save(order);

            // Publica evento de pagamento rejeitado
            eventPublisher.publishPaymentRejected(order, paymentEvent.reason());

            log.info("📤 Order {} rejected due to payment failure", order.getId());
        }
    }
}
