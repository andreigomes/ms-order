package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para o record CreateOrderCommand da interface CreateOrderUseCase
 */
class CreateOrderUseCaseTest {

    @Test
    void shouldCreateCreateOrderCommandWithAllFields() {
        // Given
        CustomerId customerId = new CustomerId("CUST001");
        ProductId productId = ProductId.of("PROD001");
        InsuranceType category = InsuranceType.AUTO;
        SalesChannel salesChannel = SalesChannel.WEB_SITE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("150.00");
        BigDecimal insuredAmount = new BigDecimal("50000.00");
        Coverages coverages = Coverages.of(Map.of("collision", new BigDecimal("40000.00")));
        Assistances assistances = Assistances.of(List.of("24h assistance"));
        String description = "Test order";

        // When
        CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
            customerId,
            productId,
            category,
            salesChannel,
            paymentMethod,
            totalMonthlyPremiumAmount,
            insuredAmount,
            coverages,
            assistances,
            description
        );

        // Then
        assertThat(command.customerId()).isEqualTo(customerId);
        assertThat(command.productId()).isEqualTo(productId);
        assertThat(command.category()).isEqualTo(category);
        assertThat(command.salesChannel()).isEqualTo(salesChannel);
        assertThat(command.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(command.totalMonthlyPremiumAmount()).isEqualTo(totalMonthlyPremiumAmount);
        assertThat(command.insuredAmount()).isEqualTo(insuredAmount);
        assertThat(command.coverages()).isEqualTo(coverages);
        assertThat(command.assistances()).isEqualTo(assistances);
        assertThat(command.description()).isEqualTo(description);
    }

    @Test
    void shouldCreateCreateOrderCommandWithNullProductId() {
        // Given
        CustomerId customerId = new CustomerId("CUST002");
        ProductId productId = null; // ProductId pode ser nulo
        InsuranceType category = InsuranceType.HOME;
        SalesChannel salesChannel = SalesChannel.MOBILE;
        PaymentMethod paymentMethod = PaymentMethod.PIX;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("200.00");
        BigDecimal insuredAmount = new BigDecimal("100000.00");
        Coverages coverages = Coverages.of(Map.of("fire", new BigDecimal("80000.00")));
        Assistances assistances = Assistances.of(List.of("emergency"));
        String description = "Home insurance";

        // When
        CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
            customerId,
            productId,
            category,
            salesChannel,
            paymentMethod,
            totalMonthlyPremiumAmount,
            insuredAmount,
            coverages,
            assistances,
            description
        );

        // Then
        assertThat(command.customerId()).isEqualTo(customerId);
        assertThat(command.productId()).isNull();
        assertThat(command.category()).isEqualTo(category);
        assertThat(command.salesChannel()).isEqualTo(salesChannel);
        assertThat(command.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(command.totalMonthlyPremiumAmount()).isEqualTo(totalMonthlyPremiumAmount);
        assertThat(command.insuredAmount()).isEqualTo(insuredAmount);
        assertThat(command.coverages()).isEqualTo(coverages);
        assertThat(command.assistances()).isEqualTo(assistances);
        assertThat(command.description()).isEqualTo(description);
    }

    @Test
    void shouldTestCreateOrderCommandEquality() {
        // Given
        CustomerId customerId = new CustomerId("CUST003");
        ProductId productId = ProductId.of("PROD003");
        InsuranceType category = InsuranceType.LIFE;
        SalesChannel salesChannel = SalesChannel.PHONE;
        PaymentMethod paymentMethod = PaymentMethod.BOLETO;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("75.00");
        BigDecimal insuredAmount = new BigDecimal("25000.00");
        Coverages coverages = Coverages.of(Map.of("life", new BigDecimal("20000.00")));
        Assistances assistances = Assistances.of(List.of("medical"));
        String description = "Life insurance";

        CreateOrderUseCase.CreateOrderCommand command1 = new CreateOrderUseCase.CreateOrderCommand(
            customerId, productId, category, salesChannel, paymentMethod,
            totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
        );

        CreateOrderUseCase.CreateOrderCommand command2 = new CreateOrderUseCase.CreateOrderCommand(
            customerId, productId, category, salesChannel, paymentMethod,
            totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
        );

        // When & Then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        assertThat(command1.toString()).isNotNull();
    }

    @Test
    void shouldTestCreateOrderCommandInequality() {
        // Given
        CustomerId customerId1 = new CustomerId("CUST004");
        CustomerId customerId2 = new CustomerId("CUST005");
        ProductId productId = ProductId.of("PROD004");
        InsuranceType category = InsuranceType.TRAVEL;
        SalesChannel salesChannel = SalesChannel.BRANCH;
        PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;
        BigDecimal totalMonthlyPremiumAmount = new BigDecimal("300.00");
        BigDecimal insuredAmount = new BigDecimal("15000.00");
        Coverages coverages = Coverages.of(Map.of("travel", new BigDecimal("12000.00")));
        Assistances assistances = Assistances.of(List.of("travel support"));
        String description = "Travel insurance";

        CreateOrderUseCase.CreateOrderCommand command1 = new CreateOrderUseCase.CreateOrderCommand(
            customerId1, productId, category, salesChannel, paymentMethod,
            totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
        );

        CreateOrderUseCase.CreateOrderCommand command2 = new CreateOrderUseCase.CreateOrderCommand(
            customerId2, productId, category, salesChannel, paymentMethod,
            totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
        );

        // When & Then
        assertThat(command1).isNotEqualTo(command2);
        assertThat(command1.hashCode()).isNotEqualTo(command2.hashCode());
    }

    @Test
    void shouldCreateCreateOrderCommandWithAllInsuranceTypes() {
        // Testa todos os tipos de seguro para garantir cobertura completa
        for (InsuranceType type : InsuranceType.values()) {
            // Given
            CustomerId customerId = new CustomerId("CUST-" + type.name());
            ProductId productId = ProductId.of("PROD-" + type.name());
            SalesChannel salesChannel = SalesChannel.WEB_SITE;
            PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
            BigDecimal totalMonthlyPremiumAmount = new BigDecimal("100.00");
            BigDecimal insuredAmount = new BigDecimal("10000.00");
            Coverages coverages = Coverages.of(Map.of("basic", new BigDecimal("8000.00")));
            Assistances assistances = Assistances.of(List.of("basic assistance"));
            String description = "Test for " + type.name();

            // When
            CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
                customerId, productId, type, salesChannel, paymentMethod,
                totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
            );

            // Then
            assertThat(command.category()).isEqualTo(type);
            assertThat(command.customerId()).isEqualTo(customerId);
            assertThat(command.description()).isEqualTo(description);
        }
    }

    @Test
    void shouldCreateCreateOrderCommandWithAllSalesChannels() {
        // Testa todos os canais de venda para garantir cobertura completa
        for (SalesChannel channel : SalesChannel.values()) {
            // Given
            CustomerId customerId = new CustomerId("CUST-" + channel.name());
            ProductId productId = ProductId.of("PROD-" + channel.name());
            InsuranceType category = InsuranceType.AUTO;
            PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
            BigDecimal totalMonthlyPremiumAmount = new BigDecimal("100.00");
            BigDecimal insuredAmount = new BigDecimal("10000.00");
            Coverages coverages = Coverages.of(Map.of("basic", new BigDecimal("8000.00")));
            Assistances assistances = Assistances.of(List.of("basic assistance"));
            String description = "Test for " + channel.name();

            // When
            CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
                customerId, productId, category, channel, paymentMethod,
                totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
            );

            // Then
            assertThat(command.salesChannel()).isEqualTo(channel);
            assertThat(command.customerId()).isEqualTo(customerId);
            assertThat(command.description()).isEqualTo(description);
        }
    }

    @Test
    void shouldCreateCreateOrderCommandWithAllPaymentMethods() {
        // Testa todos os métodos de pagamento para garantir cobertura completa
        for (PaymentMethod method : PaymentMethod.values()) {
            // Given
            CustomerId customerId = new CustomerId("CUST-" + method.name());
            ProductId productId = ProductId.of("PROD-" + method.name());
            InsuranceType category = InsuranceType.AUTO;
            SalesChannel salesChannel = SalesChannel.WEB_SITE;
            BigDecimal totalMonthlyPremiumAmount = new BigDecimal("100.00");
            BigDecimal insuredAmount = new BigDecimal("10000.00");
            Coverages coverages = Coverages.of(Map.of("basic", new BigDecimal("8000.00")));
            Assistances assistances = Assistances.of(List.of("basic assistance"));
            String description = "Test for " + method.name();

            // When
            CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
                customerId, productId, category, salesChannel, method,
                totalMonthlyPremiumAmount, insuredAmount, coverages, assistances, description
            );

            // Then
            assertThat(command.paymentMethod()).isEqualTo(method);
            assertThat(command.customerId()).isEqualTo(customerId);
            assertThat(command.description()).isEqualTo(description);
        }
    }
}
