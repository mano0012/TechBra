package com.techbra.bff.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
// TODO: Adicionar a URL do front que vai ter permissão para chamar
// @CrossOrigin(origins = {"http://localhost:8080", "https://app.cliente.com"})
public class OrderController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Orders endpoint - Em desenvolvimento");
        response.put("status", "POC");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order creation - Em desenvolvimento");
        response.put("orderId", "ORDER-" + System.currentTimeMillis());
        response.put("status", "POC");
        return ResponseEntity.ok(response);
    }
}