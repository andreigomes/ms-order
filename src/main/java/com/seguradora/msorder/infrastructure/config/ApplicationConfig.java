package com.seguradora.msorder.infrastructure.config;

import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.in.GetOrderUseCase;
import com.seguradora.msorder.core.port.in.ListOrdersUseCase;
import com.seguradora.msorder.core.port.in.UpdateOrderStatusUseCase;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import com.seguradora.msorder.core.usecase.order.CreateOrderService;
import com.seguradora.msorder.core.usecase.order.GetOrderService;
import com.seguradora.msorder.core.usecase.order.ListOrdersService;
import com.seguradora.msorder.core.usecase.order.UpdateOrderStatusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração da aplicação com beans dos casos de uso
 */
@Configuration
@EnableTransactionManagement
public class ApplicationConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepositoryPort orderRepository,
                                               OrderEventPublisherPort eventPublisher,
                                               FraudAnalysisPort fraudAnalysisPort) {
        return new CreateOrderService(orderRepository, eventPublisher, fraudAnalysisPort);
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
    public UpdateOrderStatusUseCase updateOrderStatusUseCase(OrderRepositoryPort orderRepository,
                                                           OrderEventPublisherPort eventPublisher) {
        return new UpdateOrderStatusService(orderRepository, eventPublisher);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
