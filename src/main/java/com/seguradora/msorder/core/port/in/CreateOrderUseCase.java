package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Caso de uso para criação de pedidos de seguro
 */
public interface CreateOrderUseCase {

    /**
     * Cria um novo pedido de seguro
     */
    Order createOrder(CreateOrderCommand command);

    /**
     * Comando para criação de pedido conforme especificação da API
     */
    record CreateOrderCommand(
        CustomerId customerId,
        ProductId productId,
        InsuranceType category,
        SalesChannel salesChannel,
        PaymentMethod paymentMethod,
        BigDecimal totalMonthlyPremiumAmount,
        BigDecimal insuredAmount,
        Coverages coverages,
        Assistances assistances,
        String description
    ) {}
}
