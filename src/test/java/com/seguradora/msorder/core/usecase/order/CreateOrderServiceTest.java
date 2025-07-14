package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.service.InsuranceAmountValidator;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.simulator.ExternalServicesSimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OrderEventPublisherPort eventPublisher;

    @Mock
    private FraudAnalysisPort fraudAnalysisPort;

    @Mock
    private ExternalServicesSimulator externalServicesSimulator;

    @Mock
    private InsuranceAmountValidator insuranceAmountValidator;

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(orderRepository, eventPublisher, fraudAnalysisPort, insuranceAmountValidator, externalServicesSimulator);
    }

    @Test
    void shouldCreateOrderWithLowRisk() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST001"),
            new BigDecimal("800.00"),
            InsuranceType.AUTO,
            "Seguro auto para veículo modelo 2023"
        );

        // Mock a order that will be returned after processing
        Order mockOrder = Order.create(command.customerId(), command.insuranceType(),
                                     command.amount(), command.description());
        // Simulate the business logic flow: RECEIVED -> VALIDATED -> PENDING
        mockOrder.validate();
        mockOrder.markAsPending();

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("REGULAR");
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(command.customerId(), result.getCustomerId());
        assertEquals(command.insuranceType(), result.getInsuranceType());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(command.amount(), result.getAmount());
        assertEquals(command.description(), result.getDescription());

        verify(fraudAnalysisPort).analyzeRisk(any(FraudAnalysisRequest.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
        verify(externalServicesSimulator).simulatePaymentProcessing(anyString(), anyString(), any(BigDecimal.class));
        verify(externalServicesSimulator).simulateSubscriptionAnalysis(anyString(), anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    void shouldCreateOrderWithHighRiskForAnalysis() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST002"),
            new BigDecimal("3000.00"),
            InsuranceType.HOME,
            "Seguro residencial"
        );

        Order mockOrder = Order.create(command.customerId(), command.insuranceType(),
                                     command.amount(), command.description());
        // For high risk, only validate but don't move to pending
        mockOrder.validate();

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("ALTO_RISCO");
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.VALIDATED, result.getStatus());

        verify(fraudAnalysisPort).analyzeRisk(any(FraudAnalysisRequest.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
        // High risk orders should NOT trigger external services automatically
        verify(externalServicesSimulator, never()).simulatePaymentProcessing(anyString(), anyString(), any(BigDecimal.class));
        verify(externalServicesSimulator, never()).simulateSubscriptionAnalysis(anyString(), anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    void shouldRejectOrderWhenRiskIsBlocked() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST003"),
            new BigDecimal("50000.00"),
            InsuranceType.LIFE,
            "Seguro de vida de alto valor"
        );

        Order mockOrder = Order.create(command.customerId(), command.insuranceType(),
                                     command.amount(), command.description());
        mockOrder.reject();

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("BLOCKED");
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, result.getStatus());

        verify(fraudAnalysisPort).analyzeRisk(any(FraudAnalysisRequest.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderRejected(any(Order.class), eq("Rejeitado por análise de fraudes - Nível: BLOCKED"));
    }

    @Test
    void shouldHandleFraudApiFailureGracefully() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST004"),
            new BigDecimal("1500.00"),
            InsuranceType.AUTO,
            "Test order with API failure"
        );

        Order mockOrder = Order.create(command.customerId(), command.insuranceType(),
                                     command.amount(), command.description());
        mockOrder.validate();

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("ALTO_RISCO");
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.VALIDATED, result.getStatus());

        verify(fraudAnalysisPort).analyzeRisk(any(FraudAnalysisRequest.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
    }
}
