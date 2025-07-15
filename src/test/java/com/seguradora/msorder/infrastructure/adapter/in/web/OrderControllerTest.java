package com.seguradora.msorder.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.application.mapper.OrderMapper;
import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.*;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.in.GetOrderUseCase;
import com.seguradora.msorder.core.port.in.ListOrdersUseCase;
import com.seguradora.msorder.core.port.in.UpdateOrderStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private ListOrdersUseCase listOrdersUseCase;

    @MockBean
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @MockBean
    private OrderMapper orderMapper;

    private Order mockOrder;
    private OrderResponse mockOrderResponse;
    private CreateOrderRequest mockCreateRequest;

    @BeforeEach
    void setUp() {
        // Setup mock objects
        mockOrder = createMockOrder();
        mockOrderResponse = createMockOrderResponse();
        mockCreateRequest = createMockCreateRequest();
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        // Given
        CreateOrderUseCase.CreateOrderCommand mockCommand = mock(CreateOrderUseCase.CreateOrderCommand.class);
        when(orderMapper.toCreateCommand(any(CreateOrderRequest.class)))
            .thenReturn(mockCommand);
        when(createOrderUseCase.createOrder(any(CreateOrderUseCase.CreateOrderCommand.class))).thenReturn(mockOrder);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockOrderResponse.id()))
                .andExpect(jsonPath("$.customerId").value(mockOrderResponse.customerId()));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        when(getOrderUseCase.getOrderById(any(GetOrderUseCase.GetOrderQuery.class)))
            .thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockOrderResponse.id()))
                .andExpect(jsonPath("$.customerId").value(mockOrderResponse.customerId()));
    }

    @Test
    void shouldGetAllOrdersSuccessfully() throws Exception {
        // Given
        List<Order> orders = List.of(mockOrder);

        when(listOrdersUseCase.getAllOrders()).thenReturn(orders);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldGetOrdersByCustomerSuccessfully() throws Exception {
        // Given
        String customerId = "customer-123";
        List<Order> orders = List.of(mockOrder);

        when(listOrdersUseCase.getOrdersByCustomer(any(ListOrdersUseCase.GetOrdersByCustomerQuery.class)))
            .thenReturn(orders);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldGetOrdersByStatusSuccessfully() throws Exception {
        // Given
        OrderStatus status = OrderStatus.PENDING;
        List<Order> orders = List.of(mockOrder);

        when(listOrdersUseCase.getOrdersByStatus(any(ListOrdersUseCase.GetOrdersByStatusQuery.class)))
            .thenReturn(orders);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/orders/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldApproveOrderSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        when(updateOrderStatusUseCase.approveOrder(any(UpdateOrderStatusUseCase.ApproveOrderCommand.class)))
            .thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/orders/{orderId}/approve", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldRejectOrderSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        when(updateOrderStatusUseCase.rejectOrder(any(UpdateOrderStatusUseCase.RejectOrderCommand.class)))
            .thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/orders/{orderId}/reject", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldCancelOrderSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        when(updateOrderStatusUseCase.cancelOrder(any(UpdateOrderStatusUseCase.CancelOrderCommand.class)))
            .thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldProcessOrderSuccessfully() throws Exception {
        // Given
        String orderId = "123e4567-e89b-12d3-a456-426614174000";
        when(updateOrderStatusUseCase.processOrder(any(UpdateOrderStatusUseCase.ProcessOrderCommand.class)))
            .thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/orders/{orderId}/process", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockOrderResponse.id()));
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersFound() throws Exception {
        // Given
        when(listOrdersUseCase.getAllOrders()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersFoundByCustomer() throws Exception {
        // Given
        String customerId = "nonexistent-customer";
        when(listOrdersUseCase.getOrdersByCustomer(any(ListOrdersUseCase.GetOrdersByCustomerQuery.class)))
            .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersFoundByStatus() throws Exception {
        // Given
        OrderStatus status = OrderStatus.APPROVED;
        when(listOrdersUseCase.getOrdersByStatus(any(ListOrdersUseCase.GetOrdersByStatusQuery.class)))
            .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/orders/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    private Order createMockOrder() {
        return Order.create(
            new CustomerId("customer-123"),
            ProductId.of("product-456"),
            InsuranceType.AUTO,
            SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("100000.00"),
            Coverages.of(Map.of("Collision", new BigDecimal("50000"))),
            Assistances.of(List.of("24h Roadside Assistance")),
            "Test order description"
        );
    }

    private OrderResponse createMockOrderResponse() {
        return new OrderResponse(
            "123e4567-e89b-12d3-a456-426614174000",
            "customer-123",
            "product-456",
            InsuranceType.AUTO,
            SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("500.00"),
            new BigDecimal("100000.00"),
            Map.of("Collision", new BigDecimal("50000")),
            List.of("24h Roadside Assistance"),
            OrderStatus.RECEIVED,
            "Test order description",
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            List.of()
        );
    }

    private CreateOrderRequest createMockCreateRequest() {
        return new CreateOrderRequest(
            "customer-123",
            "product-456",
            InsuranceType.AUTO,
            SalesChannel.MOBILE,
            PaymentMethod.CREDIT_CARD,
            new BigDecimal("100000.00"),
            new BigDecimal("500.00"),
            Map.of("Collision", new BigDecimal("50000")),
            List.of("24h Roadside Assistance"),
            "Test order description"
        );
    }
}
