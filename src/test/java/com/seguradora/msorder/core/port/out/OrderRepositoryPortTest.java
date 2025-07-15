package com.seguradora.msorder.core.port.out;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes para verificar o contrato da interface OrderRepositoryPort
 */
class OrderRepositoryPortTest {

    @Test
    void shouldSaveOrder() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        Order order = createTestOrder();

        when(repository.save(any(Order.class))).thenReturn(order);

        // When
        Order savedOrder = repository.save(order);

        // Then
        assertThat(savedOrder).isEqualTo(order);
        verify(repository).save(order);
    }

    @Test
    void shouldFindOrderById() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        OrderId orderId = OrderId.generate();
        Order order = createTestOrder();

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        Optional<Order> foundOrder = repository.findById(orderId);

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get()).isEqualTo(order);
        verify(repository).findById(orderId);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        OrderId orderId = OrderId.generate();

        when(repository.findById(orderId)).thenReturn(Optional.empty());

        // When
        Optional<Order> foundOrder = repository.findById(orderId);

        // Then
        assertThat(foundOrder).isEmpty();
        verify(repository).findById(orderId);
    }

    @Test
    void shouldFindOrdersByCustomerId() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        CustomerId customerId = new CustomerId("CUST001");
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        List<Order> orders = Arrays.asList(order1, order2);

        when(repository.findByCustomerId(customerId)).thenReturn(orders);

        // When
        List<Order> foundOrders = repository.findByCustomerId(customerId);

        // Then
        assertThat(foundOrders).hasSize(2);
        assertThat(foundOrders).containsExactly(order1, order2);
        verify(repository).findByCustomerId(customerId);
    }

    @Test
    void shouldFindOrdersByStatus() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        OrderStatus status = OrderStatus.PENDING;
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        List<Order> orders = Arrays.asList(order1, order2);

        when(repository.findByStatus(status)).thenReturn(orders);

        // When
        List<Order> foundOrders = repository.findByStatus(status);

        // Then
        assertThat(foundOrders).hasSize(2);
        assertThat(foundOrders).containsExactly(order1, order2);
        verify(repository).findByStatus(status);
    }

    @Test
    void shouldFindAllOrders() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        Order order3 = createTestOrder();
        List<Order> orders = Arrays.asList(order1, order2, order3);

        when(repository.findAll()).thenReturn(orders);

        // When
        List<Order> allOrders = repository.findAll();

        // Then
        assertThat(allOrders).hasSize(3);
        assertThat(allOrders).containsExactly(order1, order2, order3);
        verify(repository).findAll();
    }

    @Test
    void shouldDeleteOrderById() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        OrderId orderId = OrderId.generate();

        doNothing().when(repository).deleteById(orderId);

        // When
        repository.deleteById(orderId);

        // Then
        verify(repository).deleteById(orderId);
    }

    @Test
    void shouldCheckIfOrderExists() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        OrderId orderId = OrderId.generate();

        when(repository.existsById(orderId)).thenReturn(true);

        // When
        boolean exists = repository.existsById(orderId);

        // Then
        assertThat(exists).isTrue();
        verify(repository).existsById(orderId);
    }

    @Test
    void shouldReturnFalseWhenOrderDoesNotExist() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        OrderId orderId = OrderId.generate();

        when(repository.existsById(orderId)).thenReturn(false);

        // When
        boolean exists = repository.existsById(orderId);

        // Then
        assertThat(exists).isFalse();
        verify(repository).existsById(orderId);
    }

    @Test
    void shouldHandleEmptyLists() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        CustomerId customerId = new CustomerId("EMPTY_CUST");
        OrderStatus status = OrderStatus.CANCELLED;

        when(repository.findByCustomerId(customerId)).thenReturn(List.of());
        when(repository.findByStatus(status)).thenReturn(List.of());
        when(repository.findAll()).thenReturn(List.of());

        // When
        List<Order> ordersByCustomer = repository.findByCustomerId(customerId);
        List<Order> ordersByStatus = repository.findByStatus(status);
        List<Order> allOrders = repository.findAll();

        // Then
        assertThat(ordersByCustomer).isEmpty();
        assertThat(ordersByStatus).isEmpty();
        assertThat(allOrders).isEmpty();

        verify(repository).findByCustomerId(customerId);
        verify(repository).findByStatus(status);
        verify(repository).findAll();
    }

    @Test
    void shouldTestAllOrderStatuses() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);

        // Testa busca por todos os status possíveis
        for (OrderStatus status : OrderStatus.values()) {
            Order order = createTestOrder();
            List<Order> orders = List.of(order);

            when(repository.findByStatus(status)).thenReturn(orders);

            // When
            List<Order> foundOrders = repository.findByStatus(status);

            // Then
            assertThat(foundOrders).hasSize(1);
            assertThat(foundOrders.get(0)).isEqualTo(order);
            verify(repository).findByStatus(status);
        }
    }

    @Test
    void shouldVerifyMethodSignatures() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);

        // When & Then - Verifica que todos os métodos existem na interface
        assertThat(repository).isNotNull();

        java.lang.reflect.Method[] methods = OrderRepositoryPort.class.getDeclaredMethods();
        assertThat(methods).hasSize(7);

        // Verifica os nomes e assinaturas dos métodos
        String[] expectedMethods = {
            "save", "findById", "findByCustomerId", "findByStatus",
            "findAll", "deleteById", "existsById"
        };

        for (String methodName : expectedMethods) {
            boolean methodExists = false;
            for (java.lang.reflect.Method method : methods) {
                if (method.getName().equals(methodName)) {
                    methodExists = true;

                    // Verifica assinaturas específicas
                    switch (methodName) {
                        case "save":
                            assertThat(method.getParameterCount()).isEqualTo(1);
                            assertThat(method.getParameterTypes()[0]).isEqualTo(Order.class);
                            assertThat(method.getReturnType()).isEqualTo(Order.class);
                            break;
                        case "findById":
                            assertThat(method.getParameterCount()).isEqualTo(1);
                            assertThat(method.getParameterTypes()[0]).isEqualTo(OrderId.class);
                            assertThat(method.getReturnType()).isEqualTo(Optional.class);
                            break;
                        case "findByCustomerId":
                            assertThat(method.getParameterCount()).isEqualTo(1);
                            assertThat(method.getParameterTypes()[0]).isEqualTo(CustomerId.class);
                            assertThat(method.getReturnType()).isEqualTo(List.class);
                            break;
                        case "findByStatus":
                            assertThat(method.getParameterCount()).isEqualTo(1);
                            assertThat(method.getParameterTypes()[0]).isEqualTo(OrderStatus.class);
                            assertThat(method.getReturnType()).isEqualTo(List.class);
                            break;
                        case "findAll":
                            assertThat(method.getParameterCount()).isEqualTo(0);
                            assertThat(method.getReturnType()).isEqualTo(List.class);
                            break;
                        case "deleteById":
                            assertThat(method.getParameterCount()).isEqualTo(1);
                            assertThat(method.getParameterTypes()[0]).isEqualTo(OrderId.class);
                            assertThat(method.getReturnType()).isEqualTo(void.class);
                            break;
                        case "existsById":
                            assertThat(method.getParameterCount()).isEqualTo(1);
                            assertThat(method.getParameterTypes()[0]).isEqualTo(OrderId.class);
                            assertThat(method.getReturnType()).isEqualTo(boolean.class);
                            break;
                    }
                    break;
                }
            }
            assertThat(methodExists).as("Method %s should exist", methodName).isTrue();
        }
    }

    @Test
    void shouldHandleMultipleCustomers() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        CustomerId[] customerIds = {
            new CustomerId("CUST001"),
            new CustomerId("CUST002"),
            new CustomerId("CUST003")
        };

        for (CustomerId customerId : customerIds) {
            Order order = createTestOrder();
            List<Order> orders = List.of(order);

            when(repository.findByCustomerId(customerId)).thenReturn(orders);

            // When
            List<Order> foundOrders = repository.findByCustomerId(customerId);

            // Then
            assertThat(foundOrders).hasSize(1);
            verify(repository).findByCustomerId(customerId);
        }
    }

    @Test
    void shouldHandleCompleteRepositoryWorkflow() {
        // Given
        OrderRepositoryPort repository = Mockito.mock(OrderRepositoryPort.class);
        Order order = createTestOrder();
        OrderId orderId = order.getId();
        CustomerId customerId = order.getCustomerId();

        // Setup mocks para um fluxo completo
        when(repository.save(order)).thenReturn(order);
        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(repository.findByCustomerId(customerId)).thenReturn(List.of(order));
        when(repository.existsById(orderId)).thenReturn(true);
        doNothing().when(repository).deleteById(orderId);

        // When - Executa um fluxo completo
        Order savedOrder = repository.save(order);
        Optional<Order> foundOrder = repository.findById(orderId);
        List<Order> customerOrders = repository.findByCustomerId(customerId);
        boolean exists = repository.existsById(orderId);
        repository.deleteById(orderId);

        // Then
        assertThat(savedOrder).isEqualTo(order);
        assertThat(foundOrder).isPresent();
        assertThat(customerOrders).hasSize(1);
        assertThat(exists).isTrue();

        verify(repository).save(order);
        verify(repository).findById(orderId);
        verify(repository).findByCustomerId(customerId);
        verify(repository).existsById(orderId);
        verify(repository).deleteById(orderId);
    }

    private Order createTestOrder() {
        return Order.create(
            new CustomerId("TEST_CUSTOMER"),
            ProductId.of("TEST_PRODUCT"),
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("150.00"),
            new BigDecimal("50000.00"),
            Coverages.of(Map.of("collision", new BigDecimal("40000.00"))),
            Assistances.of(List.of("24h assistance")),
            "Test order description"
        );
    }
}
