package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.service.InsuranceAmountValidator;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.simulator.ExternalServicesSimulatorInterface;
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
    private final InsuranceAmountValidator amountValidator;
    private final ExternalServicesSimulatorInterface externalServicesSimulator;

    public CreateOrderService(OrderRepositoryPort orderRepository,
                             OrderEventPublisherPort eventPublisher,
                             FraudAnalysisPort fraudAnalysisPort,
                             InsuranceAmountValidator amountValidator,
                             ExternalServicesSimulatorInterface externalServicesSimulator) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.fraudAnalysisPort = fraudAnalysisPort;
        this.amountValidator = amountValidator;
        this.externalServicesSimulator = externalServicesSimulator;
    }

    @Override
    public Order createOrder(CreateOrderCommand command) {
        logger.info("Iniciando criação de pedido para customer: {}", command.customerId());

        // 1. Criar entidade Order com status inicial RECEIVED
        Order order = Order.create(
            command.customerId(),
            command.insuranceType(),
            command.amount(),
            command.description()
        );

        // 2. Persistir com status RECEIVED e publicar evento
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderCreated(savedOrder);
        logger.info("Pedido criado com status RECEIVED - ID: {}", savedOrder.getId().getValue());

        // 3. Preparar requisição para análise de fraudes
        FraudAnalysisRequest fraudRequest = new FraudAnalysisRequest(
            command.customerId().getValue(),
            command.amount(),
            command.insuranceType().name(),
            command.description()
        );

        // 4. Consultar API de fraudes para análise de risco
        String riskLevelStr = fraudAnalysisPort.analyzeRisk(fraudRequest);
        RiskLevel riskLevel = RiskLevel.fromString(riskLevelStr);
        logger.info("Nível de risco retornado: {} para customer: {}", riskLevel, command.customerId());

        // 5. Aplicar regras de negócio baseadas no nível de risco e valor
        Order validatedOrder = applyRiskAndAmountValidationRules(savedOrder, riskLevel);

        // 6. Se passou na validação, alterar para PENDING (fora do método de validação)
        if (validatedOrder.getStatus() == OrderStatus.VALIDATED) {
            validatedOrder.markAsPending(); // VALIDATED -> PENDING
            logger.info("Order {} aprovada para processamento - mudando para PENDING", validatedOrder.getId().getValue());

            // Salvar mudança de status no banco e publicar evento
            Order pendingOrder = orderRepository.save(validatedOrder);
            eventPublisher.publishOrderPendingAnalysis(pendingOrder, riskLevel.name());

            // Trigger external services simulation after moving to PENDING
            triggerExternalServices(pendingOrder);

            logger.info("Pedido movido para PENDING - ID: {}, Status: {}, Risk: {}",
                       pendingOrder.getId().getValue(), pendingOrder.getStatus(), riskLevel);

            return pendingOrder;
        }

        logger.info("Pedido processado com sucesso - ID: {}, Status: {}, Risk: {}",
                   validatedOrder.getId().getValue(), validatedOrder.getStatus(), riskLevel);

        return validatedOrder;
    }

    /**
     * Aplica regras de validação baseadas no nível de risco e valor do seguro
     * IMPORTANTE: Cada mudança de status persiste no banco e publica evento
     */
    private Order applyRiskAndAmountValidationRules(Order order, RiskLevel riskLevel) {
        // Validar se o valor está dentro dos limites para o tipo de cliente
        boolean isAmountValid = amountValidator.isAmountValid(riskLevel, order.getInsuranceType(), order.getAmount());

        if (isAmountValid) {
            // Se o valor é válido, sempre marca como VALIDATED
            order.validate(); // RECEIVED -> VALIDATED
            logger.info("Análise de fraudes aprovada - Order {} validada com risco: {}",
                       order.getId().getValue(), riskLevel);

            // Salvar mudança de status no banco e publicar evento
            Order savedOrder = orderRepository.save(order);
            eventPublisher.publishOrderValidated(savedOrder);
            return savedOrder;
        } else {
            // Valor acima do limite permitido - rejeitar diretamente
            order.reject(); // RECEIVED -> REJECTED
            logger.warn("Valor {} acima do limite para cliente {} - Rejeitado: {}",
                       order.getAmount(), riskLevel, order.getId().getValue());

            // Salvar mudança de status no banco e publicar evento
            Order savedOrder = orderRepository.save(order);
            eventPublisher.publishOrderRejected(savedOrder, "Valor acima do limite permitido para o tipo de cliente");
            return savedOrder;
        }
    }

    private void triggerExternalServices(Order order) {
        // Simulação de chamada para serviços externos após o pedido ser movido para PENDING
        logger.info("Chamando serviços externos para o pedido ID: {}", order.getId().getValue());
        externalServicesSimulator.simulatePaymentProcessing(
            order.getId().getValue().toString(),
            order.getCustomerId().getValue(),
            order.getAmount()
        );
        externalServicesSimulator.simulateSubscriptionAnalysis(
            order.getId().getValue().toString(),
            order.getCustomerId().getValue(),
            order.getInsuranceType().name(),
            order.getAmount()
        );
    }
}
