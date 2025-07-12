package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
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

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(orderRepository, eventPublisher, fraudAnalysisPort);
    }

    @Test
    void shouldCreateOrderWithLowRisk() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST001"),
            InsuranceType.AUTO,
            new BigDecimal("800.00"), // Valor baixo para simular baixo risco
            "Seguro auto para veículo modelo 2023"
        );

        Order savedOrder = Order.create(command.customerId(), command.insuranceType(),
                                       command.amount(), command.description());
        savedOrder.updateStatus(OrderStatus.PENDING_PAYMENT);

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any(Order.class))).thenReturn("LOW");
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(command.customerId(), result.getCustomerId());
        assertEquals(command.insuranceType(), result.getInsuranceType());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());
        assertEquals(command.amount(), result.getAmount());
        assertEquals(command.description(), result.getDescription());

        verify(fraudAnalysisPort).isCustomerBlocked("CUST001");
        verify(fraudAnalysisPort).analyzeRisk(any(Order.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithMediumRiskForAnalysis() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST002"),
            InsuranceType.HOME,
            new BigDecimal("3000.00"), // Valor médio para simular risco médio
            "Seguro residencial"
        );

        Order savedOrder = Order.create(command.customerId(), command.insuranceType(),
                                       command.amount(), command.description());
        savedOrder.updateStatus(OrderStatus.PENDING_ANALYSIS);

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any(Order.class))).thenReturn("MEDIUM");
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING_ANALYSIS, result.getStatus());

        verify(fraudAnalysisPort).isCustomerBlocked("CUST002");
        verify(fraudAnalysisPort).analyzeRisk(any(Order.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderPendingAnalysis(any(Order.class), eq("MEDIUM"));
    }

    @Test
    void shouldRejectOrderWhenCustomerIsBlocked() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("BLOCKED_CUSTOMER_001"),
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Tentativa de pedido por cliente bloqueado"
        );

        Order savedOrder = Order.create(command.customerId(), command.insuranceType(),
                                       command.amount(), command.description());
        savedOrder.updateStatus(OrderStatus.REJECTED);

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, result.getStatus());

        verify(fraudAnalysisPort).isCustomerBlocked("BLOCKED_CUSTOMER_001");
        verify(fraudAnalysisPort, never()).analyzeRisk(any(Order.class)); // Não deve chamar análise se bloqueado
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderRejected(any(Order.class), eq("Cliente bloqueado por fraude"));
    }

    @Test
    void shouldRejectOrderWhenRiskIsBlocked() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST003"),
            InsuranceType.LIFE,
            new BigDecimal("50000.00"), // Valor muito alto para simular risco bloqueado
            "Seguro de vida de alto valor"
        );

        Order savedOrder = Order.create(command.customerId(), command.insuranceType(),
                                       command.amount(), command.description());
        savedOrder.updateStatus(OrderStatus.REJECTED);

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any(Order.class))).thenReturn("BLOCKED");
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, result.getStatus());

        verify(fraudAnalysisPort).isCustomerBlocked("CUST003");
        verify(fraudAnalysisPort).analyzeRisk(any(Order.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderRejected(any(Order.class), eq("Alto risco de fraude - Nível: BLOCKED"));
    }

    @Test
    void shouldHandleFraudApiFailureGracefully() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST004"),
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Test order with API failure"
        );

        Order savedOrder = Order.create(command.customerId(), command.insuranceType(),
                                       command.amount(), command.description());
        savedOrder.updateStatus(OrderStatus.PENDING_ANALYSIS);

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any(Order.class))).thenReturn("HIGH"); // Fallback para alto risco
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING_ANALYSIS, result.getStatus());

        verify(fraudAnalysisPort).isCustomerBlocked("CUST004");
        verify(fraudAnalysisPort).analyzeRisk(any(Order.class));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderPendingAnalysis(any(Order.class), eq("HIGH"));
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST001"),
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Test order"
        );

        when(fraudAnalysisPort.isCustomerBlocked(anyString())).thenReturn(false);
        when(fraudAnalysisPort.analyzeRisk(any(Order.class))).thenReturn("LOW");
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> createOrderService.createOrder(command));

        verify(fraudAnalysisPort).isCustomerBlocked("CUST001");
        verify(fraudAnalysisPort).analyzeRisk(any(Order.class));
        verify(orderRepository).save(any(Order.class));
        verifyNoInteractions(eventPublisher);
    }
}
