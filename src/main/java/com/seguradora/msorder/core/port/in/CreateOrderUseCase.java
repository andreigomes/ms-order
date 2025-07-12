package com.seguradora.msorder.core.port.in;

import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.InsuranceType;

import java.math.BigDecimal;

/**
 * Port de entrada para criação de pedidos
 */
public interface CreateOrderUseCase {

    /**
     * Cria um novo pedido de seguro
     * @param command dados do pedido a ser criado
     * @return pedido criado
     */
    Order createOrder(CreateOrderCommand command);

    /**
     * Command object para criação de pedido
     */
    record CreateOrderCommand(
        CustomerId customerId,
        InsuranceType insuranceType,
        BigDecimal amount,
        String description
    ) {}
}
