package com.primpact.testproject.controller;

import com.primpact.testproject.model.UserDto;
import com.primpact.testproject.model.UserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// CHANGE SCENARIO: API BREAKING
@RestController
@RequestMapping("/api/users")
public class UserController {

    // API BREAKING: DELETE endpoint REMOVED (CRITICAL!)
    // Was: @DeleteMapping("/{id}") public ResponseEntity<Void> deleteUser(...)

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

    // API BREAKING: Return type CHANGED from UserModel to UserDto (MEDIUM!)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long id) {
        UserDto user = new UserDto(id, "user" + id, "User " + id);
        return ResponseEntity.ok(user);
    }

    // API BREAKING: New endpoint ADDED (LOW severity)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel user) {
        user.setId(1L);
        return ResponseEntity.ok(user);
    }

    // API BREAKING: Parameter CHANGED - added required 'active' parameter (HIGH!)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/status")
    public ResponseEntity<String> getUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(active ? "active" : "inactive");
    }
}
