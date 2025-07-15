package com.seguradora.msorder.core.usecase.order;

/**
 * Exceção lançada quando há falha na criação de um pedido
 */
public class OrderCreationException extends RuntimeException {

    public OrderCreationException(String message) {
        super(message);
    }

    public OrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
