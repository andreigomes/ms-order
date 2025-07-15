package com.seguradora.msorder.infrastructure.adapter.out.persistence;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import com.seguradora.msorder.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @Mock
    private OrderPersistenceMapper mapper;

    @InjectMocks
    private OrderPersistenceAdapter orderPersistenceAdapter;

    private Order mockOrder;
    private OrderJpaEntity mockJpaEntity;
    private OrderId orderId;
    private CustomerId customerId;

    @BeforeEach
    void setUp() {
        orderId = OrderId.of("123e4567-e89b-12d3-a456-426614174000");
        customerId = new CustomerId("customer-123");

        mockOrder = Order.create(
            customerId,
            ProductId.of("product-456"),
            InsuranceType.AUTO,
            SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("100000.00"),
            Coverages.of(Map.of("Collision", new BigDecimal("50000"))),
            Assistances.of(List.of("24h Roadside Assistance")),
            "Test order"
        );

        mockJpaEntity = new OrderJpaEntity();
        mockJpaEntity.setId(orderId.getValue().toString());
        mockJpaEntity.setCustomerId(customerId.getValue());
    }

    @Test
    void shouldSaveOrderSuccessfully() {
        // Given
        when(mapper.toJpaEntity(mockOrder)).thenReturn(mockJpaEntity);
        when(jpaRepository.save(mockJpaEntity)).thenReturn(mockJpaEntity);
        when(mapper.restoreDomain(mockJpaEntity)).thenReturn(mockOrder);

        // When
        Order savedOrder = orderPersistenceAdapter.save(mockOrder);

        // Then
        assertThat(savedOrder).isEqualTo(mockOrder);
        verify(mapper).toJpaEntity(mockOrder);
        verify(jpaRepository).save(mockJpaEntity);
        verify(mapper).restoreDomain(mockJpaEntity);
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {
        // Given
        when(jpaRepository.findById(orderId.getValue())).thenReturn(Optional.of(mockJpaEntity));
        when(mapper.restoreDomain(mockJpaEntity)).thenReturn(mockOrder);

        // When
        Optional<Order> foundOrder = orderPersistenceAdapter.findById(orderId);

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get()).isEqualTo(mockOrder);
        verify(jpaRepository).findById(orderId.getValue());
        verify(mapper).restoreDomain(mockJpaEntity);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFoundById() {
        // Given
        when(jpaRepository.findById(orderId.getValue())).thenReturn(Optional.empty());

        // When
        Optional<Order> foundOrder = orderPersistenceAdapter.findById(orderId);

        // Then
        assertThat(foundOrder).isEmpty();
        verify(jpaRepository).findById(orderId.getValue());
        verify(mapper, never()).restoreDomain(any());
    }

    @Test
    void shouldFindOrdersByCustomerIdSuccessfully() {
        // Given
        List<OrderJpaEntity> jpaEntities = List.of(mockJpaEntity);
        when(jpaRepository.findByCustomerId(customerId.getValue())).thenReturn(jpaEntities);
        when(mapper.restoreDomain(mockJpaEntity)).thenReturn(mockOrder);

        // When
        List<Order> foundOrders = orderPersistenceAdapter.findByCustomerId(customerId);

        // Then
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0)).isEqualTo(mockOrder);
        verify(jpaRepository).findByCustomerId(customerId.getValue());
        verify(mapper).restoreDomain(mockJpaEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersFoundByCustomerId() {
        // Given
        when(jpaRepository.findByCustomerId(customerId.getValue())).thenReturn(List.of());

        // When
        List<Order> foundOrders = orderPersistenceAdapter.findByCustomerId(customerId);

        // Then
        assertThat(foundOrders).isEmpty();
        verify(jpaRepository).findByCustomerId(customerId.getValue());
        verify(mapper, never()).restoreDomain(any());
    }

    @Test
    void shouldFindOrdersByStatusSuccessfully() {
        // Given
        OrderStatus status = OrderStatus.PENDING;
        List<OrderJpaEntity> jpaEntities = List.of(mockJpaEntity);
        when(jpaRepository.findByStatus(status)).thenReturn(jpaEntities);
        when(mapper.restoreDomain(mockJpaEntity)).thenReturn(mockOrder);

        // When
        List<Order> foundOrders = orderPersistenceAdapter.findByStatus(status);

        // Then
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0)).isEqualTo(mockOrder);
        verify(jpaRepository).findByStatus(status);
        verify(mapper).restoreDomain(mockJpaEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersFoundByStatus() {
        // Given
        OrderStatus status = OrderStatus.APPROVED;
        when(jpaRepository.findByStatus(status)).thenReturn(List.of());

        // When
        List<Order> foundOrders = orderPersistenceAdapter.findByStatus(status);

        // Then
        assertThat(foundOrders).isEmpty();
        verify(jpaRepository).findByStatus(status);
        verify(mapper, never()).restoreDomain(any());
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        // Given
        List<OrderJpaEntity> jpaEntities = List.of(mockJpaEntity);
        when(jpaRepository.findAll()).thenReturn(jpaEntities);
        when(mapper.restoreDomain(mockJpaEntity)).thenReturn(mockOrder);

        // When
        List<Order> foundOrders = orderPersistenceAdapter.findAll();

        // Then
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0)).isEqualTo(mockOrder);
        verify(jpaRepository).findAll();
        verify(mapper).restoreDomain(mockJpaEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersExist() {
        // Given
        when(jpaRepository.findAll()).thenReturn(List.of());

        // When
        List<Order> foundOrders = orderPersistenceAdapter.findAll();

        // Then
        assertThat(foundOrders).isEmpty();
        verify(jpaRepository).findAll();
        verify(mapper, never()).restoreDomain(any());
    }

    @Test
    void shouldDeleteOrderByIdSuccessfully() {
        // When
        orderPersistenceAdapter.deleteById(orderId);

        // Then
        verify(jpaRepository).deleteById(orderId.getValue());
    }

    @Test
    void shouldCheckIfOrderExistsById() {
        // Given
        when(jpaRepository.existsById(orderId.getValue())).thenReturn(true);

        // When
        boolean exists = orderPersistenceAdapter.existsById(orderId);

        // Then
        assertThat(exists).isTrue();
        verify(jpaRepository).existsById(orderId.getValue());
    }

    @Test
    void shouldReturnFalseWhenOrderDoesNotExist() {
        // Given
        when(jpaRepository.existsById(orderId.getValue())).thenReturn(false);

        // When
        boolean exists = orderPersistenceAdapter.existsById(orderId);

        // Then
        assertThat(exists).isFalse();
        verify(jpaRepository).existsById(orderId.getValue());
    }
}
