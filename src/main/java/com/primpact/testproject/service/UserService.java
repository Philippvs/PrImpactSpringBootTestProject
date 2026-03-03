package com.primpact.testproject.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

// CHANGE SCENARIO: ARCHITECTURE
// ARCHITECTURE VIOLATION: Circular dependency with OrderService!
@Service
public class UserService {

    // CIRCULAR DEPENDENCY: UserService -> OrderService -> UserService
    private final OrderService orderService;

    public UserService(@Lazy OrderService orderService) {
        this.orderService = orderService;
    }
    
    public String getUserById(Long id) {
        return "User " + id;
    }

    public void deleteUser(Long id) {
        // Delete logic
    }

    // Method that creates circular call pattern
    public int getUserOrderCount(Long userId) {
        return orderService.getAllOrders().size();
    }
}
