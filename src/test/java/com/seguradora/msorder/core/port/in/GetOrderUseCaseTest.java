package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.valueobject.OrderId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para o record GetOrderQuery da interface GetOrderUseCase
 */
class GetOrderUseCaseTest {

    @Test
    void shouldCreateGetOrderQueryWithValidOrderId() {
        // Given
        OrderId orderId = OrderId.generate();

        // When
        GetOrderUseCase.GetOrderQuery query = new GetOrderUseCase.GetOrderQuery(orderId);

        // Then
        assertThat(query.orderId()).isEqualTo(orderId);
        assertThat(query.orderId()).isNotNull();
    }

    @Test
    void shouldCreateGetOrderQueryWithNullOrderId() {
        // Given
        OrderId orderId = null;

        // When
        GetOrderUseCase.GetOrderQuery query = new GetOrderUseCase.GetOrderQuery(orderId);

        // Then
        assertThat(query.orderId()).isNull();
    }

    @Test
    void shouldTestGetOrderQueryEquality() {
        // Given
        OrderId orderId = OrderId.generate();
        GetOrderUseCase.GetOrderQuery query1 = new GetOrderUseCase.GetOrderQuery(orderId);
        GetOrderUseCase.GetOrderQuery query2 = new GetOrderUseCase.GetOrderQuery(orderId);

        // When & Then
        assertThat(query1).isEqualTo(query2);
        assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
        assertThat(query1.toString()).isNotNull();
        assertThat(query1.toString()).contains(orderId.toString());
    }

    @Test
    void shouldTestGetOrderQueryInequality() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();
        GetOrderUseCase.GetOrderQuery query1 = new GetOrderUseCase.GetOrderQuery(orderId1);
        GetOrderUseCase.GetOrderQuery query2 = new GetOrderUseCase.GetOrderQuery(orderId2);

        // When & Then
        assertThat(query1).isNotEqualTo(query2);
        assertThat(query1.hashCode()).isNotEqualTo(query2.hashCode());
    }

    @Test
    void shouldCreateGetOrderQueryWithSpecificOrderId() {
        // Given
        OrderId orderId = OrderId.of("550e8400-e29b-41d4-a716-446655440000");

        // When
        GetOrderUseCase.GetOrderQuery query = new GetOrderUseCase.GetOrderQuery(orderId);

        // Then
        assertThat(query.orderId()).isEqualTo(orderId);
        assertThat(query.orderId().getValue().toString()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
    }

    @Test
    void shouldTestMultipleGetOrderQueries() {
        // Given
        OrderId[] orderIds = {
            OrderId.generate(),
            OrderId.generate(),
            OrderId.generate()
        };

        // When
        GetOrderUseCase.GetOrderQuery[] queries = new GetOrderUseCase.GetOrderQuery[orderIds.length];
        for (int i = 0; i < orderIds.length; i++) {
            queries[i] = new GetOrderUseCase.GetOrderQuery(orderIds[i]);
        }

        // Then
        for (int i = 0; i < queries.length; i++) {
            assertThat(queries[i].orderId()).isEqualTo(orderIds[i]);
        }

        // Verifica que queries diferentes não são iguais
        assertThat(queries[0]).isNotEqualTo(queries[1]);
        assertThat(queries[1]).isNotEqualTo(queries[2]);
        assertThat(queries[0]).isNotEqualTo(queries[2]);
    }
}
