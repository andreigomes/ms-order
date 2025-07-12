package com.seguradora.msorder.application.dto;

import com.seguradora.msorder.core.domain.valueobject.InsuranceType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para criação de pedidos
 */
public record CreateOrderRequest(

    @NotBlank(message = "Customer ID is required")
    String customerId,

    @NotNull(message = "Insurance type is required")
    InsuranceType insuranceType,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,

    String description
) {}
