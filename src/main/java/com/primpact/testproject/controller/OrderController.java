package com.primpact.testproject.controller;

import com.primpact.testproject.entity.OrderEntity;
import com.primpact.testproject.model.OrderRequest;
import com.primpact.testproject.model.OrderResponse;
import com.primpact.testproject.repository.OrderRepository;
import com.primpact.testproject.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CHANGE SCENARIO: ARCHITECTURE - Controller now depends on Repository directly!
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    // ARCHITECTURE VIOLATION: Controller -> Repository (bypassing service layer)
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    // CHANGE SCENARIO: API BREAKING
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.processOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return orderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // CHANGE SCENARIO: API BREAKING
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            OrderResponse response = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // CHANGE SCENARIO: CONFIG
    @GetMapping("/export/status")
    public ResponseEntity<Boolean> getExportStatus() {
        return ResponseEntity.ok(orderService.isExportEnabled());
    }

    // ARCHITECTURE VIOLATION: Direct repository access from controller
    @GetMapping("/raw/{id}")
    public ResponseEntity<OrderEntity> getRawOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ARCHITECTURE VIOLATION: Direct repository access for count
    @GetMapping("/count")
    public ResponseEntity<Long> getOrderCount() {
        return ResponseEntity.ok(orderRepository.count());
    }
}
