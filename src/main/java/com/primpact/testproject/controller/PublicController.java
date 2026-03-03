package com.primpact.testproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// SECURITY TEST POINT - New unsecured controller (no @PreAuthorize!)
@RestController
@RequestMapping("/api/public")
public class PublicController {

    // NEW_UNSECURED_ENDPOINT - No security annotation!
    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        return ResponseEntity.ok("public data");
    }

    // NEW_UNSECURED_ENDPOINT - No security annotation!
    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("public info");
    }

    // NEW_UNSECURED_ENDPOINT - No security annotation!
    @PostMapping("/feedback")
    public ResponseEntity<String> submitFeedback(@RequestBody String feedback) {
        return ResponseEntity.ok("Feedback received");
    }
}
