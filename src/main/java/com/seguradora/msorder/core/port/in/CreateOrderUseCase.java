package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;

import java.math.BigDecimal;

/**
 * Caso de uso para criação de pedidos de seguro
 */
public interface CreateOrderUseCase {

    /**
     * Cria um novo pedido de seguro
     */
    Order createOrder(CreateOrderCommand command);

    /**
     * Comando para criação de pedido
     */
    record CreateOrderCommand(
        CustomerId customerId,
        BigDecimal amount,
        InsuranceType insuranceType,
        String description
    ) {}
}
