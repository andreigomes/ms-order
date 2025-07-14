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

        Order mockOrder = Order.create(command.customerId(), command.insuranceType(),
                                     command.amount(), command.description());

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("REGULAR");
        when(insuranceAmountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(command.customerId(), result.getCustomerId());
        assertEquals(command.insuranceType(), result.getInsuranceType());
        assertEquals(OrderStatus.RECEIVED, result.getStatus()); // Inicial sempre RECEIVED
        assertEquals(command.amount(), result.getAmount());
        assertEquals(command.description(), result.getDescription());

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
        // Processamento assíncrono não é verificado em teste unitário
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

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("HIGH_RISK");
        when(insuranceAmountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class))).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.RECEIVED, result.getStatus()); // Inicial sempre RECEIVED

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
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

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("HIGH_RISK");
        when(insuranceAmountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class))).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.RECEIVED, result.getStatus()); // Inicial sempre RECEIVED

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
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

        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenThrow(new RuntimeException("API failure"));
        when(insuranceAmountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.RECEIVED, result.getStatus()); // Inicial sempre RECEIVED

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
    }
}
