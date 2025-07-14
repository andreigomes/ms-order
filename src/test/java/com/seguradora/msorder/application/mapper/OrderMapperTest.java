package com.seguradora.msorder.application.mapper;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase.CreateOrderCommand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapperImpl();

    @Test
    void shouldMapCreateRequestToCreateCommand() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "123",
            "PROD001",
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("50000.00"),
            Map.of("collision", new BigDecimal("40000.00")),
            List.of("24h assistance"),
            "Test order"
        );

        // When
        CreateOrderCommand command = mapper.toCreateCommand(request);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.customerId().getValue()).isEqualTo("123");
        assertThat(command.productId().getValue()).isEqualTo("PROD001");
        assertThat(command.category()).isEqualTo(InsuranceType.AUTO);
        assertThat(command.salesChannel()).isEqualTo(SalesChannel.WEB_SITE);
        assertThat(command.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(command.totalMonthlyPremiumAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(command.insuredAmount()).isEqualTo(new BigDecimal("50000.00"));
        assertThat(command.coverages().getCoverageMap()).containsEntry("collision", new BigDecimal("40000.00"));
        assertThat(command.assistances().getAssistanceList()).containsExactly("24h assistance");
        assertThat(command.description()).isEqualTo("Test order");
    }

    @Test
    void shouldMapOrderToResponse() {
        // Given
        Order order = Order.create(
                new CustomerId("123"),
                ProductId.of("PROD001"),
                InsuranceType.AUTO,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("500.00"),
                new BigDecimal("50000.00"),
                Coverages.of(Map.of("collision", new BigDecimal("40000.00"))),
                Assistances.of(List.of("24h assistance")),
                "Test order"
        );

        // When
        OrderResponse response = mapper.toResponse(order);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.customerId()).isEqualTo("123");
        assertThat(response.status()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.totalMonthlyPremiumAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(response.insuredAmount()).isEqualTo(new BigDecimal("50000.00"));
    }

    @Test
    void shouldHandleNullRequest() {
        // When
        CreateOrderCommand command = mapper.toCreateCommand(null);

        // Then
        assertThat(command).isNull();
    }

    @Test
    void shouldMapCreateRequestWithEmptyCollections() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "123",
            "PROD001",
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("50000.00"),
            Map.of(),
            List.of(),
            "Test order"
        );

        // When
        CreateOrderCommand command = mapper.toCreateCommand(request);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.coverages().getCoverageMap()).isEmpty();
        assertThat(command.assistances().getAssistanceList()).isEmpty();
    }

    @Test
    void shouldMapCreateRequestWithNullValues() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "123",
            "PROD001",
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("50000.00"),
            null,
            null,
            "Test order"
        );

        // When
        CreateOrderCommand command = mapper.toCreateCommand(request);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.customerId().getValue()).isEqualTo("123");
        assertThat(command.description()).isEqualTo("Test order");
    }
}
