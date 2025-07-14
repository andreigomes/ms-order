package com.seguradora.msorder.integration.config;

import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.infrastructure.adapter.out.messaging.simulator.ExternalServicesSimulator;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuração de teste que mocka componentes Kafka para evitar problemas de conexão
 */
@TestConfiguration
@Profile("test")
public class TestMessagingConfig {

    @Bean
    @Primary
    public ExternalServicesSimulator mockExternalServicesSimulator() {
        return Mockito.mock(ExternalServicesSimulator.class);
    }

    @Bean
    @Primary
    public OrderEventPublisherPort mockOrderEventPublisher() {
        return Mockito.mock(OrderEventPublisherPort.class);
    }
}
