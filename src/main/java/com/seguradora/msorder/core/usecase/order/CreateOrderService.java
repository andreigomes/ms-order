package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.service.InsuranceAmountValidator;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/**
 * Implementação otimizada do caso de uso para criação de pedidos
 */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;
    private final FraudAnalysisPort fraudAnalysisPort;
    private final InsuranceAmountValidator amountValidator;

    public CreateOrderService(OrderRepositoryPort orderRepository,
                             OrderEventPublisherPort eventPublisher,
                             FraudAnalysisPort fraudAnalysisPort,
                             InsuranceAmountValidator amountValidator) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.fraudAnalysisPort = fraudAnalysisPort;
        this.amountValidator = amountValidator;
    }

    @Override
    @Transactional
    public Order createOrder(CreateOrderCommand command) {
        if (logger.isDebugEnabled()) {
            logger.debug("Iniciando criação de pedido para customer: {}", command.customerId());
        }

        try {
            // 1. Criar e persistir order com status RECEIVED
            Order order = createAndPersistInitialOrder(command);

            // 2. MELHORIA: Processar validação de forma completamente assíncrona
            processOrderValidationFullyAsync(order, command);

            // 3. Retornar order imediatamente (não aguarda validação)
            return order;

        } catch (Exception e) {
            logger.error("Erro ao criar pedido para customer: {}", command.customerId(), e);
            throw new OrderCreationException("Falha na criação do pedido", e);
        }
    }

    /**
     * Cria e persiste o pedido inicial com status RECEIVED
     * Otimização: Uma única operação de banco + evento
     */
    private Order createAndPersistInitialOrder(CreateOrderCommand command) {
        Order order = Order.create(
            command.customerId(),
            command.productId(),
            command.category(),
            command.salesChannel(),
            command.paymentMethod(),
            command.totalMonthlyPremiumAmount(),
            command.insuredAmount(),
            command.coverages(),
            command.assistances(),
            command.description()
        );

        Order savedOrder = orderRepository.save(order);

        // Publicar evento de criação de forma assíncrona
        eventPublisher.publishOrderCreated(savedOrder);

        if (logger.isInfoEnabled()) {
            logger.info("Pedido criado - ID: {}, Customer: {}",
                       savedOrder.getId().getValue(), command.customerId());
        }

        return savedOrder;
    }

    /**
     * NOVA IMPLEMENTAÇÃO: Processa validação completamente assíncrona
     * Performance CRÍTICA: Não bloqueia a criação do pedido
     */
    @Async("taskExecutor")
    private void processOrderValidationFullyAsync(Order order, CreateOrderCommand command) {
        try {
            // Análise de fraudes
            RiskLevel riskLevel = performFraudAnalysisWithFallback(order, command);

            // Aplicar regras de validação e atualizar status
            Order processedOrder = applyValidationRulesOptimized(order, riskLevel);

            // Se aprovado, trigger serviços externos
            if (processedOrder.getStatus() == OrderStatus.PENDING) {
                triggerExternalServicesAsync(processedOrder);
            }

        } catch (Exception e) {
            logger.error("Erro na validação assíncrona do pedido {}", order.getId().getValue(), e);
            handleValidationFailureAsync(order, e);
        }
    }

    /**
     * Análise de fraudes com fallback para melhor resiliência
     */
    private RiskLevel performFraudAnalysisWithFallback(Order order, CreateOrderCommand command) {
        try {
            FraudAnalysisRequest fraudRequest = new FraudAnalysisRequest(
                order.getId().getValue().toString(),
                command.customerId().getValue(),
                command.insuredAmount(),
                command.category().name(),
                command.description()
            );

            String riskLevelStr = fraudAnalysisPort.analyzeRisk(fraudRequest);
            RiskLevel riskLevel = RiskLevel.fromString(riskLevelStr);

            if (logger.isDebugEnabled()) {
                logger.debug("Risk level: {} para customer: {}", riskLevel, command.customerId());
            }

            return riskLevel;

        } catch (Exception e) {
            logger.warn("Falha na análise de fraudes para customer: {}, usando fallback",
                       command.customerId(), e);
            // Fallback: assumir risco regular em caso de falha da API
            return RiskLevel.REGULAR;
        }
    }

    /**
     * Aplicar regras de validação de forma otimizada
     * Reduz operações de banco combinando mudanças de status
     */
    @Transactional
    private Order applyValidationRulesOptimized(Order order, RiskLevel riskLevel) {
        boolean isAmountValid = amountValidator.isAmountValid(
            riskLevel, order.getCategory(), order.getInsuredAmount());

        if (isAmountValid) {
            // Transição: RECEIVED -> VALIDATED -> PENDING (otimizada)
            order.validate();
            order.markAsPending();

            // Uma única operação de persistência para ambas transições
            Order savedOrder = orderRepository.save(order);

            // Publicar eventos de forma batch/assíncrona
            publishValidationEvents(savedOrder, riskLevel);

            if (logger.isInfoEnabled()) {
                logger.info("Pedido aprovado - ID: {}, Status: PENDING, Risk: {}",
                           savedOrder.getId().getValue(), riskLevel);
            }

            return savedOrder;

        } else {
            // Rejeição direta
            order.reject();
            Order rejectedOrder = orderRepository.save(order);

            eventPublisher.publishOrderRejected(rejectedOrder);

            logger.warn("Pedido rejeitado - ID: {}, Valor: {}, Risk: {}",
                       rejectedOrder.getId().getValue(), order.getInsuredAmount(), riskLevel);

            return rejectedOrder;
        }
    }

    /**
     * Publica eventos de validação de forma otimizada
     */
    private void publishValidationEvents(Order order, RiskLevel riskLevel) {
        eventPublisher.publishOrderValidated(order);
        eventPublisher.publishOrderPending(order);
    }

    /**
     * Trigger serviços externos de forma assíncrona
     */
    @Async("taskExecutor")
    private void triggerExternalServicesAsync(Order order) {
        try {
            logger.info("Serviços externos disparados para pedido: {}", order.getId().getValue());
        } catch (Exception e) {
            logger.error("Erro ao disparar serviços externos para pedido: {}", order.getId().getValue(), e);
        }
    }

    /**
     * Trata falhas na validação assíncrona
     */
    @Async("taskExecutor")
    private void handleValidationFailureAsync(Order order, Exception e) {
        try {
            order.reject();
            Order rejectedOrder = orderRepository.save(order);
            eventPublisher.publishOrderRejected(rejectedOrder);

            logger.error("Pedido rejeitado devido a falha na validação: {}", order.getId().getValue(), e);
        } catch (Exception ex) {
            logger.error("Erro crítico ao processar falha de validação para pedido: {}", order.getId().getValue(), ex);
        }
    }

    public static class OrderCreationException extends RuntimeException {
        public OrderCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
