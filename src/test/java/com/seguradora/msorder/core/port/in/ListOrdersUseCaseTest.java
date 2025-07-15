package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para os records da interface ListOrdersUseCase
 */
class ListOrdersUseCaseTest {

    @Test
    void shouldCreateGetOrdersByCustomerQueryWithValidCustomerId() {
        // Given
        CustomerId customerId = new CustomerId("CUST001");

        // When
        ListOrdersUseCase.GetOrdersByCustomerQuery query =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId);

        // Then
        assertThat(query.customerId()).isEqualTo(customerId);
        assertThat(query.customerId().getValue()).isEqualTo("CUST001");
    }

    @Test
    void shouldCreateGetOrdersByCustomerQueryWithNullCustomerId() {
        // Given
        CustomerId customerId = null;

        // When
        ListOrdersUseCase.GetOrdersByCustomerQuery query =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId);

        // Then
        assertThat(query.customerId()).isNull();
    }

    @Test
    void shouldTestGetOrdersByCustomerQueryEquality() {
        // Given
        CustomerId customerId = new CustomerId("CUST002");
        ListOrdersUseCase.GetOrdersByCustomerQuery query1 =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId);
        ListOrdersUseCase.GetOrdersByCustomerQuery query2 =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId);

        // When & Then
        assertThat(query1).isEqualTo(query2);
        assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
        assertThat(query1.toString()).isNotNull();
        assertThat(query1.toString()).contains("CUST002");
    }

    @Test
    void shouldTestGetOrdersByCustomerQueryInequality() {
        // Given
        CustomerId customerId1 = new CustomerId("CUST003");
        CustomerId customerId2 = new CustomerId("CUST004");
        ListOrdersUseCase.GetOrdersByCustomerQuery query1 =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId1);
        ListOrdersUseCase.GetOrdersByCustomerQuery query2 =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId2);

        // When & Then
        assertThat(query1).isNotEqualTo(query2);
        assertThat(query1.hashCode()).isNotEqualTo(query2.hashCode());
    }

    @Test
    void shouldCreateGetOrdersByStatusQueryWithValidStatus() {
        // Given
        OrderStatus status = OrderStatus.PENDING;

        // When
        ListOrdersUseCase.GetOrdersByStatusQuery query =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status);

        // Then
        assertThat(query.status()).isEqualTo(status);
        assertThat(query.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldCreateGetOrdersByStatusQueryWithNullStatus() {
        // Given
        OrderStatus status = null;

        // When
        ListOrdersUseCase.GetOrdersByStatusQuery query =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status);

        // Then
        assertThat(query.status()).isNull();
    }

    @Test
    void shouldTestGetOrdersByStatusQueryEquality() {
        // Given
        OrderStatus status = OrderStatus.APPROVED;
        ListOrdersUseCase.GetOrdersByStatusQuery query1 =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status);
        ListOrdersUseCase.GetOrdersByStatusQuery query2 =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status);

        // When & Then
        assertThat(query1).isEqualTo(query2);
        assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
        assertThat(query1.toString()).isNotNull();
        assertThat(query1.toString()).contains("APPROVED");
    }

    @Test
    void shouldTestGetOrdersByStatusQueryInequality() {
        // Given
        OrderStatus status1 = OrderStatus.RECEIVED;
        OrderStatus status2 = OrderStatus.REJECTED;
        ListOrdersUseCase.GetOrdersByStatusQuery query1 =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status1);
        ListOrdersUseCase.GetOrdersByStatusQuery query2 =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status2);

        // When & Then
        assertThat(query1).isNotEqualTo(query2);
        assertThat(query1.hashCode()).isNotEqualTo(query2.hashCode());
    }

    @Test
    void shouldCreateGetOrdersByStatusQueryWithAllOrderStatuses() {
        // Testa todos os status de pedido para garantir cobertura completa
        for (OrderStatus status : OrderStatus.values()) {
            // Given & When
            ListOrdersUseCase.GetOrdersByStatusQuery query =
                new ListOrdersUseCase.GetOrdersByStatusQuery(status);

            // Then
            assertThat(query.status()).isEqualTo(status);
            assertThat(query.toString()).contains(status.name());
        }
    }

    @Test
    void shouldCreateGetOrdersByCustomerQueryWithDifferentCustomerIds() {
        // Given
        String[] customerIds = {"CUST-001", "CUST-002", "CUST-003", "customer@email.com", "12345"};

        for (String customerId : customerIds) {
            // When
            CustomerId customer = new CustomerId(customerId);
            ListOrdersUseCase.GetOrdersByCustomerQuery query =
                new ListOrdersUseCase.GetOrdersByCustomerQuery(customer);

            // Then
            assertThat(query.customerId().getValue()).isEqualTo(customerId);
            assertThat(query.toString()).contains(customerId);
        }
    }

    @Test
    void shouldTestCombinedQueries() {
        // Given
        CustomerId customerId = new CustomerId("COMBINED-TEST");
        OrderStatus status = OrderStatus.VALIDATED;

        // When
        ListOrdersUseCase.GetOrdersByCustomerQuery customerQuery =
            new ListOrdersUseCase.GetOrdersByCustomerQuery(customerId);
        ListOrdersUseCase.GetOrdersByStatusQuery statusQuery =
            new ListOrdersUseCase.GetOrdersByStatusQuery(status);

        // Then
        assertThat(customerQuery.customerId()).isEqualTo(customerId);
        assertThat(statusQuery.status()).isEqualTo(status);

        // Verifica que são diferentes tipos de query
        assertThat(customerQuery.getClass()).isNotEqualTo(statusQuery.getClass());
    }
}
