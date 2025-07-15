package com.seguradora.msorder.core.usecase.coordination;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por coordenar os eventos de pagamento e subscrição
 * Só finaliza o pedido quando ambos estiverem aprovados
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
     * Processa aprovação de pagamento e verifica se pode finalizar o pedido
     */
    public void processPaymentApproval(String orderId) {
        logger.info("Processando aprovação de pagamento para pedido: {}", orderId);

        Order order = findOrderById(orderId);
        boolean canFinalize = order.approvePayment();

        orderRepository.save(order);

        if (canFinalize) {
            finalizeOrder(order);
        } else {
            logger.info("Pagamento aprovado para pedido {}, aguardando aprovação de subscrição", orderId);
        }
    }

    /**
     * Processa rejeição de pagamento
     */
    public void processPaymentRejection(String orderId, String reason) {
        logger.info("Processando rejeição de pagamento para pedido: {} - Motivo: {}", orderId, reason);

        Order order = findOrderById(orderId);
        order.rejectPayment(reason);

        orderRepository.save(order);
        eventPublisher.publishOrderRejected(order);

        logger.info("Pedido {} rejeitado devido a pagamento negado", orderId);
    }

    /**
     * Processa aprovação de subscrição e verifica se pode finalizar o pedido
     */
    public void processSubscriptionApproval(String orderId) {
        logger.info("Processando aprovação de subscrição para pedido: {}", orderId);

        Order order = findOrderById(orderId);
        boolean canFinalize = order.approveSubscription();

        orderRepository.save(order);

        if (canFinalize) {
            finalizeOrder(order);
        } else {
            logger.info("Subscrição aprovada para pedido {}, aguardando aprovação de pagamento", orderId);
        }
    }

    /**
     * Processa rejeição de subscrição
     */
    public void processSubscriptionRejection(String orderId, String reason) {
        logger.info("Processando rejeição de subscrição para pedido: {} - Motivo: {}", orderId, reason);

        Order order = findOrderById(orderId);
        order.rejectSubscription(reason);

        orderRepository.save(order);
        eventPublisher.publishOrderRejected(order);

        logger.info("Pedido {} rejeitado devido a subscrição negada", orderId);
    }

    /**
     * Finaliza o pedido quando ambos pagamento e subscrição foram aprovados
     */
    private void finalizeOrder(Order order) {
        logger.info("Finalizando pedido {} - ambos pagamento e subscrição aprovados", order.getId().getValue());

        order.finalizeApproval();
        orderRepository.save(order);
        eventPublisher.publishOrderApproved(order);

        logger.info("Pedido {} finalizado com sucesso", order.getId().getValue());
    }

    /**
     * Busca pedido por ID e valida existência
     */
    private Order findOrderById(String orderId) {
        return orderRepository.findById(OrderId.of(orderId))
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + orderId));
    }
}
