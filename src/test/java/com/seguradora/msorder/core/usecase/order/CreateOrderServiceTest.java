package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.service.InsuranceAmountValidator;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.infrastructure.adapter.out.external.dto.FraudAnalysisRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private InsuranceAmountValidator amountValidator;

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(
            orderRepository,
            eventPublisher,
            fraudAnalysisPort,
            amountValidator
        );
    }

    @Test
    void shouldCreateOrderWithLowRiskAndProceedToPending() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("1001"),
            ProductId.of("PROD001"),
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("80.00"),
            new BigDecimal("800.00"),
            Coverages.of(Map.of("collision", new BigDecimal("600.00"))),
            Assistances.of(List.of("24h assistance")),
            "Seguro auto para veículo modelo 2023"
        );

        Order mockOrder = Order.create(command.customerId(), command.productId(), command.category(),
                                     command.salesChannel(), command.paymentMethod(),
                                     command.totalMonthlyPremiumAmount(), command.insuredAmount(),
                                     command.coverages(), command.assistances(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("PREFERENTIAL");
        when(amountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class)))
                .thenReturn(true);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(command.customerId(), result.getCustomerId());
        assertEquals(command.category(), result.getCategory());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(command.insuredAmount(), result.getInsuredAmount());
        assertEquals(command.description(), result.getDescription());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderValidated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderPending(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithHighRiskAndProceedToPending() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("1002"),
            ProductId.of("PROD002"),
            InsuranceType.HOME,
            SalesChannel.PHONE,
            PaymentMethod.BANK_TRANSFER,
            new BigDecimal("300.00"),
            new BigDecimal("3000.00"),
            Coverages.of(Map.of("fire", new BigDecimal("2500.00"))),
            Assistances.of(List.of("emergency repair")),
            "Seguro residencial"
        );

        Order mockOrder = Order.create(command.customerId(), command.productId(), command.category(),
                                     command.salesChannel(), command.paymentMethod(),
                                     command.totalMonthlyPremiumAmount(), command.insuredAmount(),
                                     command.coverages(), command.assistances(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("HIGH_RISK");
        when(amountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class)))
                .thenReturn(true);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderValidated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderPending(any(Order.class));
    }

    @Test
    void shouldCreateOrderAndRejectWhenAmountIsInvalid() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("1003"),
            ProductId.of("PROD003"),
            InsuranceType.LIFE,
            SalesChannel.BRANCH,
            PaymentMethod.PIX,
            new BigDecimal("500.00"),
            new BigDecimal("50000.00"),
            Coverages.of(Map.of("death", new BigDecimal("50000.00"))),
            Assistances.of(List.of("beneficiary support")),
            "Seguro de vida de alto valor"
        );

        Order mockOrder = Order.create(command.customerId(), command.productId(), command.category(),
                                     command.salesChannel(), command.paymentMethod(),
                                     command.totalMonthlyPremiumAmount(), command.insuredAmount(),
                                     command.coverages(), command.assistances(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("HIGH_RISK");
        when(amountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class)))
                .thenReturn(false);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, result.getStatus());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderRejected(any(Order.class));
    }

    @Test
    void shouldHandleFraudApiFailureGracefullyAndUseFallback() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("1004"),
            ProductId.of("PROD004"),
            InsuranceType.AUTO,
            SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("150.00"),
            new BigDecimal("1500.00"),
            Coverages.of(Map.of("basic", new BigDecimal("1200.00"))),
            Assistances.of(List.of("roadside assistance")),
            "Test order with API failure"
        );

        Order mockOrder = Order.create(command.customerId(), command.productId(), command.category(),
                                     command.salesChannel(), command.paymentMethod(),
                                     command.totalMonthlyPremiumAmount(), command.insuredAmount(),
                                     command.coverages(), command.assistances(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class)))
                .thenThrow(new RuntimeException("API failure"));
        when(amountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class)))
                .thenReturn(true);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderValidated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderPending(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithRegularRisk() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("1005"),
            ProductId.of("PROD005"),
            InsuranceType.AUTO,
            SalesChannel.WHATSAPP,
            PaymentMethod.PIX,
            new BigDecimal("500.00"),
            new BigDecimal("5000.00"),
            Coverages.of(Map.of("collision", new BigDecimal("4000.00"))),
            Assistances.of(List.of("24h assistance")),
            "Seguro auto valor médio"
        );

        Order mockOrder = Order.create(command.customerId(), command.productId(), command.category(),
                                     command.salesChannel(), command.paymentMethod(),
                                     command.totalMonthlyPremiumAmount(), command.insuredAmount(),
                                     command.coverages(), command.assistances(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("REGULAR");
        when(amountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class)))
                .thenReturn(true);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(command.customerId(), result.getCustomerId());
        assertEquals(command.category(), result.getCategory());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(command.insuredAmount(), result.getInsuredAmount());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderValidated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderPending(any(Order.class));
    }

    @Test
    void shouldRejectOrderWithNoInfoRiskAndHighAmount() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("1006"),
            ProductId.of("PROD006"),
            InsuranceType.LIFE,
            SalesChannel.PARTNER,
            PaymentMethod.BANK_TRANSFER,
            new BigDecimal("1000.00"),
            new BigDecimal("100000.00"),
            Coverages.of(Map.of("death", new BigDecimal("100000.00"))),
            Assistances.of(List.of("premium support")),
            "Seguro de vida valor alto sem informações"
        );

        Order mockOrder = Order.create(command.customerId(), command.productId(), command.category(),
                                     command.salesChannel(), command.paymentMethod(),
                                     command.totalMonthlyPremiumAmount(), command.insuredAmount(),
                                     command.coverages(), command.assistances(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(fraudAnalysisPort.analyzeRisk(any(FraudAnalysisRequest.class))).thenReturn("NO_INFO");
        when(amountValidator.isAmountValid(any(RiskLevel.class), any(InsuranceType.class), any(BigDecimal.class)))
                .thenReturn(false);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, result.getStatus());

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(eventPublisher, times(1)).publishOrderRejected(any(Order.class));
    }
}
