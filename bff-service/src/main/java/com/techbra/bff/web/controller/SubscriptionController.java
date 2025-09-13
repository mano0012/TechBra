package com.techbra.bff.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
// TODO: Adicionar a URL do front que vai ter permissão para chamar
// @CrossOrigin(origins = {"http://localhost:8080", "https://app.cliente.com"})
public class SubscriptionController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSubscriptions() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Subscriptions endpoint - Em desenvolvimento");
        response.put("status", "POC");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSubscription(@RequestBody Map<String, Object> subscriptionData) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Subscription creation - Em desenvolvimento");
        response.put("subscriptionId", "SUB-" + System.currentTimeMillis());
        response.put("status", "POC");
        return ResponseEntity.ok(response);
    }
}