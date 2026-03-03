package com.primpact.testproject.service;

import org.springframework.stereotype.Service;

// CHANGE SCENARIO: ARCHITECTURE
// Service layer - depends on repository layer
@Service
public class UserService {

    // Architecture: controller -> service -> repository (clean layering)
    
    public String getUserById(Long id) {
        return "User " + id;
    }

    public void deleteUser(Long id) {
        // Delete logic
    }
}
