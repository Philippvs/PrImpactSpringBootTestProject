package com.primpact.testproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// CHANGE SCENARIO: SECURITY - CrossOrigin ADDED (security relaxation!)
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/data")
public class ApiController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<String> getData() {
        return ResponseEntity.ok("data");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<String> getDataById(@PathVariable Long id) {
        return ResponseEntity.ok("data-" + id);
    }
}
