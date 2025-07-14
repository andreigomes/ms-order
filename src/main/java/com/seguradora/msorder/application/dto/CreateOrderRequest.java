package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;
import com.seguradora.msorder.core.domain.valueobject.SalesChannel;
import com.seguradora.msorder.core.domain.valueobject.PaymentMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO para criação de pedidos conforme especificação da API
 */
public record CreateOrderRequest(

    @NotBlank(message = "Customer ID is required")
    String customerId,

    @NotBlank(message = "Product ID is required")
    String productId,

    @NotNull(message = "Category is required")
    InsuranceType category,

    @NotNull(message = "Sales channel is required")
    SalesChannel salesChannel,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,

    @NotNull(message = "Total monthly premium amount is required")
    @Positive(message = "Total monthly premium amount must be greater than zero")
    BigDecimal totalMonthlyPremiumAmount,

    @NotNull(message = "Insured amount is required")
    @Positive(message = "Insured amount must be greater than zero")
    BigDecimal insuredAmount,

    @NotNull(message = "Coverages are required")
    @NotEmpty(message = "Coverages cannot be empty")
    Map<String, BigDecimal> coverages,

    @NotNull(message = "Assistances are required")
    @NotEmpty(message = "Assistances cannot be empty")
    List<String> assistances,

    String description
) {}
