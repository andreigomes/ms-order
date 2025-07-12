package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do caso de uso para criação de pedidos
 */
@Service
@Transactional
public class CreateOrderService implements CreateOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;
    private final FraudAnalysisPort fraudAnalysisPort;

    public CreateOrderService(OrderRepositoryPort orderRepository,
                             OrderEventPublisherPort eventPublisher,
                             FraudAnalysisPort fraudAnalysisPort) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.fraudAnalysisPort = fraudAnalysisPort;
    }

    @Override
    public Order createOrder(CreateOrderCommand command) {
        logger.info("Iniciando criação de pedido para customer: {}", command.customerId());

        // Criar o pedido usando factory method da entidade
        Order order = Order.create(
            command.customerId(),
            command.insuranceType(),
            command.amount(),
            command.description()
        );

        // Verificar se cliente está bloqueado
        if (fraudAnalysisPort.isCustomerBlocked(command.customerId().getValue())) {
            logger.warn("Cliente bloqueado detectado: {}", command.customerId());
            order.updateStatus(OrderStatus.REJECTED);
            Order savedOrder = orderRepository.save(order);
            eventPublisher.publishOrderRejected(savedOrder, "Cliente bloqueado por fraude");
            return savedOrder;
        }

        // Consultar API de fraudes para análise de risco
        String riskLevel = fraudAnalysisPort.analyzeRisk(order);
        logger.info("Nível de risco retornado: {} para customer: {}", riskLevel, command.customerId());

        // Aplicar regras de negócio baseadas no nível de risco
        Order processedOrder = applyRiskValidationRules(order, riskLevel);

        // Persistir o pedido
        Order savedOrder = orderRepository.save(processedOrder);

        // Publicar evento baseado no status final
        publishEventBasedOnStatus(savedOrder, riskLevel);

        logger.info("Pedido criado com sucesso - ID: {}, Status: {}, Risk: {}",
                   savedOrder.getId().getValue(), savedOrder.getStatus(), riskLevel);

        return savedOrder;
    }

    /**
     * Aplica regras de validação baseadas no nível de risco
     */
    private Order applyRiskValidationRules(Order order, String riskLevel) {
        switch (riskLevel.toUpperCase()) {
            case "LOW":
                // Baixo risco: aprovação automática para análise
                order.updateStatus(OrderStatus.PENDING_PAYMENT);
                logger.info("Baixo risco - Aprovado para pagamento: {}", order.getId().getValue());
                break;

            case "MEDIUM":
                // Risco médio: requer análise manual
                order.updateStatus(OrderStatus.PENDING_ANALYSIS);
                logger.info("Risco médio - Enviado para análise manual: {}", order.getId().getValue());
                break;

            case "HIGH":
                // Alto risco: requer análise detalhada
                order.updateStatus(OrderStatus.PENDING_ANALYSIS);
                logger.warn("Alto risco - Análise detalhada necessária: {}", order.getId().getValue());
                break;

            case "BLOCKED":
                // Bloqueado: rejeição automática
                order.updateStatus(OrderStatus.REJECTED);
                logger.warn("Risco bloqueado - Pedido rejeitado: {}", order.getId().getValue());
                break;

            default:
                // Fallback para análise manual
                order.updateStatus(OrderStatus.PENDING_ANALYSIS);
                logger.warn("Nível de risco desconhecido: {} - Enviado para análise manual", riskLevel);
        }

        return order;
    }

    /**
     * Publica eventos baseados no status final do pedido
     */
    private void publishEventBasedOnStatus(Order order, String riskLevel) {
        switch (order.getStatus()) {
            case PENDING_PAYMENT:
                eventPublisher.publishOrderCreated(order);
                break;
            case PENDING_ANALYSIS:
                eventPublisher.publishOrderPendingAnalysis(order, riskLevel);
                break;
            case REJECTED:
                eventPublisher.publishOrderRejected(order, "Alto risco de fraude - Nível: " + riskLevel);
                break;
            default:
                eventPublisher.publishOrderCreated(order);
        }
    }
}
