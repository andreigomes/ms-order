package com.seguradora.msorder.core.usecase.order;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OrderEventPublisherPort eventPublisher;

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(orderRepository, eventPublisher);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId("CUST001"),
            InsuranceType.AUTO,
            new BigDecimal("1500.00"),
            "Seguro auto para veículo modelo 2023"
        );

        Order savedOrder = Order.create(command.customerId(), command.insuranceType(),
                                       command.amount(), command.description());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = createOrderService.createOrder(command);

        // Then
        assertNotNull(result);
        assertEquals(command.customerId(), result.getCustomerId());
        assertEquals(command.insuranceType(), result.getInsuranceType());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(command.amount(), result.getAmount());
        assertEquals(command.description(), result.getDescription());

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
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

        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> createOrderService.createOrder(command));
        verify(orderRepository).save(any(Order.class));
        verifyNoInteractions(eventPublisher);
    }
}
