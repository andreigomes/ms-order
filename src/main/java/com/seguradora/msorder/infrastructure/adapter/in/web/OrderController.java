package com.seguradora.msorder.infrastructure.adapter.in.web;

import com.seguradora.msorder.application.dto.CreateOrderRequest;
import com.seguradora.msorder.application.dto.OrderResponse;
import com.seguradora.msorder.application.mapper.OrderMapper;
import com.seguradora.msorder.core.domain.entity.Order;
import com.seguradora.msorder.core.domain.valueobject.CustomerId;
import com.seguradora.msorder.core.domain.valueobject.OrderId;
import com.seguradora.msorder.core.domain.valueobject.OrderStatus;
import com.seguradora.msorder.core.port.in.CreateOrderUseCase;
import com.seguradora.msorder.core.port.in.GetOrderUseCase;
import com.seguradora.msorder.core.port.in.ListOrdersUseCase;
import com.seguradora.msorder.core.port.in.UpdateOrderStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciamento de pedidos
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderMapper orderMapper;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                          GetOrderUseCase getOrderUseCase,
                          ListOrdersUseCase listOrdersUseCase,
                          UpdateOrderStatusUseCase updateOrderStatusUseCase,
                          OrderMapper orderMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var command = orderMapper.toCreateCommand(request);
        Order order = createOrderUseCase.createOrder(command);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        var query = new GetOrderUseCase.GetOrderQuery(OrderId.of(orderId));
        Order order = getOrderUseCase.getOrderById(query);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = listOrdersUseCase.getAllOrders();
        List<OrderResponse> responses = orders.stream()
            .map(orderMapper::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable String customerId) {
        var query = new ListOrdersUseCase.GetOrdersByCustomerQuery(new CustomerId(customerId));
        List<Order> orders = listOrdersUseCase.getOrdersByCustomer(query);
        List<OrderResponse> responses = orders.stream()
            .map(orderMapper::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        var query = new ListOrdersUseCase.GetOrdersByStatusQuery(status);
        List<Order> orders = listOrdersUseCase.getOrdersByStatus(query);
        List<OrderResponse> responses = orders.stream()
            .map(orderMapper::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{orderId}/approve")
    public ResponseEntity<OrderResponse> approveOrder(@PathVariable String orderId) {
        var command = new UpdateOrderStatusUseCase.ApproveOrderCommand(OrderId.of(orderId));
        Order order = updateOrderStatusUseCase.approveOrder(command);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/reject")
    public ResponseEntity<OrderResponse> rejectOrder(@PathVariable String orderId) {
        var command = new UpdateOrderStatusUseCase.RejectOrderCommand(OrderId.of(orderId));
        Order order = updateOrderStatusUseCase.rejectOrder(command);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderId) {
        var command = new UpdateOrderStatusUseCase.CancelOrderCommand(OrderId.of(orderId));
        Order order = updateOrderStatusUseCase.cancelOrder(command);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/process")
    public ResponseEntity<OrderResponse> processOrder(@PathVariable String orderId) {
        var command = new UpdateOrderStatusUseCase.ProcessOrderCommand(OrderId.of(orderId));
        Order order = updateOrderStatusUseCase.processOrder(command);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable String orderId) {
        var command = new UpdateOrderStatusUseCase.CompleteOrderCommand(OrderId.of(orderId));
        Order order = updateOrderStatusUseCase.completeOrder(command);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }
}
