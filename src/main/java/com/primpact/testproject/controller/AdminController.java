package com.primpact.testproject.controller;

import com.primpact.testproject.model.OrderResponse;
import com.primpact.testproject.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CHANGE SCENARIO: SECURITY
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    // SECURITY TEST POINT - PreAuthorize with ADMIN role
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // SECURITY TEST POINT - PreAuthorize with SUPER_ADMIN role
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/orders/all")
    public ResponseEntity<Void> deleteAllOrders() {
        return ResponseEntity.noContent().build();
    }

    // SECURITY TEST POINT - PreAuthorize for sensitive data
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders/export")
    public ResponseEntity<List<OrderResponse>> exportOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // CHANGE SCENARIO: API BREAKING
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}/edit")
    public ResponseEntity<String> initUpdateForm(@PathVariable Long id) {
        return ResponseEntity.ok("Edit form for user " + id);
    }
}
