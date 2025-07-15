package com.seguradora.msorder.infrastructure.config;

import com.seguradora.msorder.core.domain.service.InsuranceAmountValidator;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.in.GetOrderUseCase;
import com.seguradora.msorder.core.port.in.ListOrdersUseCase;
import com.seguradora.msorder.core.port.out.FraudAnalysisPort;
import com.seguradora.msorder.core.port.out.OrderEventPublisherPort;
import com.seguradora.msorder.core.port.out.OrderRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OrderEventPublisherPort eventPublisher;

    @Mock
    private FraudAnalysisPort fraudAnalysisPort;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void shouldCreateCreateOrderUseCase() {
        // Given
        InsuranceAmountValidator amountValidator = new InsuranceAmountValidator();

        // When
        CreateOrderUseCase createOrderUseCase = applicationConfig.createOrderUseCase(
            orderRepository, eventPublisher, fraudAnalysisPort, amountValidator);

        // Then
        assertThat(createOrderUseCase).isNotNull();
    }

    @Test
    void shouldCreateGetOrderUseCase() {
        // When
        GetOrderUseCase getOrderUseCase = applicationConfig.getOrderUseCase(orderRepository);

        // Then
        assertThat(getOrderUseCase).isNotNull();
    }

    @Test
    void shouldCreateListOrdersUseCase() {
        // When
        ListOrdersUseCase listOrdersUseCase = applicationConfig.listOrdersUseCase(orderRepository);

        // Then
        assertThat(listOrdersUseCase).isNotNull();
    }

    @Test
    void shouldCreateInsuranceAmountValidator() {
        // When
        InsuranceAmountValidator validator = applicationConfig.insuranceAmountValidator();

        // Then
        assertThat(validator).isNotNull();
    }
}
