package com.seguradora.msorder.core.usecase.coordination;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por coordenar eventos de pagamento e subscrição
 * Implementa controle de concorrência com retry para evitar race conditions
 */
@Service
@Transactional
public class EventCoordinationService {

    private static final Logger logger = LoggerFactory.getLogger(EventCoordinationService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public EventCoordinationService(OrderRepositoryPort orderRepository,
                                  OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Processa aprovação de pagamento com retry em caso de concorrência
     */
    @Retryable(value = {OptimisticLockingFailureException.class},
               maxAttempts = 3,
               backoff = @Backoff(delay = 100, multiplier = 2))
    @CacheEvict(value = "orders", key = "#orderId")
    public void processPaymentApproval(String orderId) {
        try {
            logger.info("Processando aprovação de pagamento para pedido: {}", orderId);

            OrderId orderIdVO = OrderId.of(orderId);
            Order order = orderRepository.findById(orderIdVO)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Aprovar pagamento e verificar se pode finalizar
            boolean canFinalize = order.approvePayment();
            orderRepository.save(order);

            // Se pode finalizar (ambos aprovados), finalizar o pedido
            if (canFinalize) {
                order.finalizeApproval();
                orderRepository.save(order);
                eventPublisher.publishOrderApproved(order);
                logger.info("Pedido {} finalizado com sucesso", orderId);
            } else {
                logger.info("Pagamento aprovado para pedido {}, aguardando aprovação de subscrição", orderId);
            }

        } catch (OptimisticLockingFailureException e) {
            logger.warn("Conflito de concorrência ao processar pagamento para pedido: {}, tentando novamente...", orderId);
            throw e; // Re-throw para trigger do retry
        } catch (Exception e) {
            logger.error("Erro ao processar aprovação de pagamento para pedido: {}", orderId, e);
            throw e;
        }
    }

    /**
     * Processa rejeição de pagamento com retry em caso de concorrência
     */
    @Retryable(value = {OptimisticLockingFailureException.class},
               maxAttempts = 3,
               backoff = @Backoff(delay = 100, multiplier = 2))
    @CacheEvict(value = "orders", key = "#orderId")
    public void processPaymentRejection(String orderId, String reason) {
        try {
            logger.info("Processando rejeição de pagamento para pedido: {}", orderId);

            OrderId orderIdVO = OrderId.of(orderId);
            Order order = orderRepository.findById(orderIdVO)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Rejeitar pagamento
            order.rejectPayment(reason != null ? reason : "Payment rejected");
            orderRepository.save(order);

            // Publicar evento de rejeição
            eventPublisher.publishOrderRejected(order);
            logger.info("Pedido {} rejeitado devido a pagamento rejeitado", orderId);

        } catch (OptimisticLockingFailureException e) {
            logger.warn("Conflito de concorrência ao rejeitar pagamento para pedido: {}, tentando novamente...", orderId);
            throw e; // Re-throw para trigger do retry
        } catch (Exception e) {
            logger.error("Erro ao processar rejeição de pagamento para pedido: {}", orderId, e);
            throw e;
        }
    }

    /**
     * Processa aprovação de subscrição com retry em caso de concorrência
     */
    @Retryable(value = {OptimisticLockingFailureException.class},
               maxAttempts = 3,
               backoff = @Backoff(delay = 100, multiplier = 2))
    @CacheEvict(value = "orders", key = "#orderId")
    public void processSubscriptionApproval(String orderId) {
        try {
            logger.info("Processando aprovação de subscrição para pedido: {}", orderId);

            OrderId orderIdVO = OrderId.of(orderId);
            Order order = orderRepository.findById(orderIdVO)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Aprovar subscrição e verificar se pode finalizar
            boolean canFinalize = order.approveSubscription();
            orderRepository.save(order);

            // Se pode finalizar (ambos aprovados), finalizar o pedido
            if (canFinalize) {
                logger.info("Finalizando pedido {} - ambos pagamento e subscrição aprovados", orderId);
                order.finalizeApproval();
                orderRepository.save(order);
                eventPublisher.publishOrderApproved(order);
                logger.info("Pedido {} finalizado com sucesso", orderId);
            } else {
                logger.info("Subscrição aprovada para pedido {}, aguardando aprovação de pagamento", orderId);
            }

        } catch (OptimisticLockingFailureException e) {
            logger.warn("Conflito de concorrência ao processar subscrição para pedido: {}, tentando novamente...", orderId);
            throw e; // Re-throw para trigger do retry
        } catch (Exception e) {
            logger.error("Erro ao processar aprovação de subscrição para pedido: {}", orderId, e);
            throw e;
        }
    }

    /**
     * Processa rejeição de subscrição com retry em caso de concorrência
     */
    @Retryable(value = {OptimisticLockingFailureException.class},
               maxAttempts = 3,
               backoff = @Backoff(delay = 100, multiplier = 2))
    @CacheEvict(value = "orders", key = "#orderId")
    public void processSubscriptionRejection(String orderId, String reason) {
        try {
            logger.info("Processando rejeição de subscrição para pedido: {}", orderId);

            OrderId orderIdVO = OrderId.of(orderId);
            Order order = orderRepository.findById(orderIdVO)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Rejeitar subscrição
            order.rejectSubscription(reason != null ? reason : "Subscription rejected");
            orderRepository.save(order);

            // Publicar evento de rejeição
            eventPublisher.publishOrderRejected(order);
            logger.info("Pedido {} rejeitado devido a subscrição rejeitada", orderId);

        } catch (OptimisticLockingFailureException e) {
            logger.warn("Conflito de concorrência ao rejeitar subscrição para pedido: {}, tentando novamente...", orderId);
            throw e; // Re-throw para trigger do retry
        } catch (Exception e) {
            logger.error("Erro ao processar rejeição de subscrição para pedido: {}", orderId, e);
            throw e;
        }
    }
}
