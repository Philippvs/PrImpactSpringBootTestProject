package com.primpact.testproject.controller;

import com.primpact.testproject.model.UserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// CHANGE SCENARIO: SECURITY
@RestController
@RequestMapping("/api/users")
public class UserController {

    // SECURITY TEST POINT - PreAuthorize on method
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUser(@PathVariable Long id) {
        UserModel user = new UserModel("user" + id, "user" + id + "@example.com");
        user.setId(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    // CHANGE SCENARIO: API BREAKING - Return type
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserModel> getUserProfile(@PathVariable Long id) {
        UserModel user = new UserModel("user" + id, "user" + id + "@example.com");
        user.setId(id);
        return ResponseEntity.ok(user);
    }
}
