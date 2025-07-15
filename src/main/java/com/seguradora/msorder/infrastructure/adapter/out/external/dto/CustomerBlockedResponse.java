package com.seguradora.msorder.infrastructure.adapter.out.external.dto;

/**
 * DTO para resposta da consulta de bloqueio de cliente
 */
public record CustomerBlockedResponse(
    String customerId,
    boolean isBlocked,
    String reason
) {}
