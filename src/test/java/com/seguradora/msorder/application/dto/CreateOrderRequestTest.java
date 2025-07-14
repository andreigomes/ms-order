package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderRequestTest {

    @Test
    void shouldCreateRequestWithAllFields() {
        // Given
        Map<String, BigDecimal> coverages = Map.of("collision", new BigDecimal("40000.00"));
        List<String> assistances = List.of("24h assistance");

        // When
        CreateOrderRequest request = new CreateOrderRequest(
            "123",
            "PROD001",
            InsuranceType.AUTO,
            SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("50000.00"),
            coverages,
            assistances,
            "Test order"
        );

        // Then
        assertThat(request.customerId()).isEqualTo("123");
        assertThat(request.productId()).isEqualTo("PROD001");
        assertThat(request.category()).isEqualTo(InsuranceType.AUTO);
        assertThat(request.salesChannel()).isEqualTo(SalesChannel.WEB_SITE);
        assertThat(request.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(request.totalMonthlyPremiumAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(request.insuredAmount()).isEqualTo(new BigDecimal("50000.00"));
        assertThat(request.coverages()).containsEntry("collision", new BigDecimal("40000.00"));
        assertThat(request.assistances()).containsExactly("24h assistance");
        assertThat(request.description()).isEqualTo("Test order");
    }

    @Test
    void shouldHandleEmptyCollections() {
        // When
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

        // Then
        assertThat(request.coverages()).isEmpty();
        assertThat(request.assistances()).isEmpty();
    }

    @Test
    void shouldHandleNullValues() {
        // When
        CreateOrderRequest request = new CreateOrderRequest(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        // Then
        assertThat(request.customerId()).isNull();
        assertThat(request.productId()).isNull();
        assertThat(request.category()).isNull();
        assertThat(request.salesChannel()).isNull();
        assertThat(request.paymentMethod()).isNull();
        assertThat(request.totalMonthlyPremiumAmount()).isNull();
        assertThat(request.insuredAmount()).isNull();
        assertThat(request.coverages()).isNull();
        assertThat(request.assistances()).isNull();
        assertThat(request.description()).isNull();
    }

    @Test
    void shouldHandleAllInsuranceTypes() {
        // When & Then
        CreateOrderRequest autoRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Auto insurance"
        );
        assertThat(autoRequest.category()).isEqualTo(InsuranceType.AUTO);

        CreateOrderRequest homeRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.HOME, SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Home insurance"
        );
        assertThat(homeRequest.category()).isEqualTo(InsuranceType.HOME);

        CreateOrderRequest lifeRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.LIFE, SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Life insurance"
        );
        assertThat(lifeRequest.category()).isEqualTo(InsuranceType.LIFE);

        CreateOrderRequest travelRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.TRAVEL, SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Travel insurance"
        );
        assertThat(travelRequest.category()).isEqualTo(InsuranceType.TRAVEL);
    }

    @Test
    void shouldHandleAllSalesChannels() {
        // When & Then
        CreateOrderRequest webRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Web order"
        );
        assertThat(webRequest.salesChannel()).isEqualTo(SalesChannel.WEB_SITE);

        CreateOrderRequest mobileRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Mobile order"
        );
        assertThat(mobileRequest.salesChannel()).isEqualTo(SalesChannel.MOBILE);

        CreateOrderRequest branchRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.BRANCH,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Branch order"
        );
        assertThat(branchRequest.salesChannel()).isEqualTo(SalesChannel.BRANCH);

        CreateOrderRequest phoneRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.PHONE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Phone order"
        );
        assertThat(phoneRequest.salesChannel()).isEqualTo(SalesChannel.PHONE);
    }

    @Test
    void shouldHandleAllPaymentMethods() {
        // When & Then
        CreateOrderRequest creditCardRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.WEB_SITE,
            PaymentMethod.CREDIT_CARD, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Credit card payment"
        );
        assertThat(creditCardRequest.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);

        CreateOrderRequest pixRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.WEB_SITE,
            PaymentMethod.PIX, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "PIX payment"
        );
        assertThat(pixRequest.paymentMethod()).isEqualTo(PaymentMethod.PIX);

        CreateOrderRequest bankTransferRequest = new CreateOrderRequest(
            "123", "PROD001", InsuranceType.AUTO, SalesChannel.WEB_SITE,
            PaymentMethod.BANK_TRANSFER, new BigDecimal("500.00"), new BigDecimal("50000.00"),
            Map.of(), List.of(), "Bank transfer payment"
        );
        assertThat(bankTransferRequest.paymentMethod()).isEqualTo(PaymentMethod.BANK_TRANSFER);
    }
}
