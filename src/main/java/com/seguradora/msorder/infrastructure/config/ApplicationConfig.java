package com.seguradora.msorder.infrastructure.config;

import com.seguradora.msorder.core.domain.service.InsuranceAmountValidator;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.in.GetOrderUseCase;
import com.seguradora.msorder.core.port.in.ListOrdersUseCase;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.core.usecase.order.CreateOrderService;
import com.seguradora.msorder.core.usecase.order.GetOrderService;
import com.seguradora.msorder.core.usecase.order.ListOrdersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuração da aplicação com beans dos casos de uso
 */
@Configuration
@EnableAsync
public class ApplicationConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepositoryPort orderRepository,
                                               OrderEventPublisherPort eventPublisher,
                                               FraudAnalysisPort fraudAnalysisPort,
                                               InsuranceAmountValidator amountValidator) {
        return new CreateOrderService(orderRepository, eventPublisher, fraudAnalysisPort, amountValidator);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepositoryPort orderRepository) {
        return new GetOrderService(orderRepository);
    }

    @Bean
    public ListOrdersUseCase listOrdersUseCase(OrderRepositoryPort orderRepository) {
        return new ListOrdersService(orderRepository);
    }

    @Bean
    public InsuranceAmountValidator insuranceAmountValidator() {
        return new InsuranceAmountValidator();
    }
}
