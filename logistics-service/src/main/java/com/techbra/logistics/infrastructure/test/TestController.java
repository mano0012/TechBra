package com.techbra.logistics.infrastructure.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller para testes e simulações
 * Facilita o teste manual do sistema sem necessidade do Kafka
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private TestEventService testEventService;
    
    /**
     * Cria um envio de teste com dados padrão
     */
    @PostMapping("/create-test-shipment")
    public ResponseEntity<Map<String, String>> createTestShipment() {
        try {
            testEventService.createTestShipment();
            return ResponseEntity.ok(Map.of(
                "message", "Envio de teste criado com sucesso",
                "orderId", "1001"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Erro ao criar envio de teste: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Cria múltiplos envios de teste
     */
    @PostMapping("/create-multiple-test-shipments")
    public ResponseEntity<Map<String, String>> createMultipleTestShipments() {
        try {
            testEventService.createMultipleTestShipments();
            return ResponseEntity.ok(Map.of(
                "message", "Múltiplos envios de teste criados com sucesso",
                "count", "3"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Erro ao criar envios de teste: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Simula um evento personalizado
     */
    @PostMapping("/simulate-order-paid")
    public ResponseEntity<Map<String, String>> simulateOrderPaid(
            @RequestParam Long orderId,
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @RequestParam String deliveryAddress,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zipCode,
            @RequestParam(defaultValue = "Brasil") String country,
            @RequestParam BigDecimal totalAmount) {
        
        try {
            testEventService.simulateOrderPaidEvent(
                orderId, customerName, customerEmail, deliveryAddress,
                city, state, zipCode, country, totalAmount
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Evento simulado com sucesso",
                "orderId", orderId.toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Erro ao simular evento: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Endpoint de informações sobre os testes disponíveis
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getTestInfo() {
        return ResponseEntity.ok(Map.of(
            "message", "Endpoints de teste disponíveis",
            "endpoints", Map.of(
                "POST /api/test/create-test-shipment", "Cria um envio de teste padrão",
                "POST /api/test/create-multiple-test-shipments", "Cria múltiplos envios de teste",
                "POST /api/test/simulate-order-paid", "Simula um evento personalizado",
                "GET /api/test/info", "Informações sobre os testes"
            ),
            "example_simulate_order_paid", Map.of(
                "url", "/api/test/simulate-order-paid",
                "method", "POST",
                "params", Map.of(
                    "orderId", "1234",
                    "customerName", "João Silva",
                    "customerEmail", "joao@email.com",
                    "deliveryAddress", "Rua das Flores, 123",
                    "city", "São Paulo",
                    "state", "SP",
                    "zipCode", "01234-567",
                    "country", "Brasil",
                    "totalAmount", "299.99"
                )
            )
        ));
    }
}