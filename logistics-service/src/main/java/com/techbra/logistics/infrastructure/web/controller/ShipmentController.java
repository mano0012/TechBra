package com.techbra.logistics.infrastructure.web.controller;

import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.model.ShipmentStatus;
import com.techbra.logistics.domain.ports.in.ShipmentUseCases;
import com.techbra.logistics.infrastructure.web.dto.ShipmentResponseDto;
import com.techbra.logistics.infrastructure.web.dto.UpdateStatusRequestDto;
import com.techbra.logistics.infrastructure.web.mapper.ShipmentWebMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);
    
    private final ShipmentUseCases shipmentUseCases;
    private final ShipmentWebMapper webMapper;
    
    public ShipmentController(ShipmentUseCases shipmentUseCases, ShipmentWebMapper webMapper) {
        this.shipmentUseCases = shipmentUseCases;
        this.webMapper = webMapper;
    }
    
    /**
     * Lista todos os envios
     */
    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
        logger.info("Requisição para listar todos os envios");
        
        List<Shipment> shipments = shipmentUseCases.findAllShipments();
        List<ShipmentResponseDto> response = webMapper.toResponseDtoList(shipments);
        
        logger.info("Retornando {} envios", response.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca um envio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Long id) {
        logger.info("Requisição para buscar envio por ID: {}", id);
        
        Optional<Shipment> shipment = shipmentUseCases.findShipmentById(id);
        
        if (shipment.isPresent()) {
            ShipmentResponseDto response = webMapper.toResponseDto(shipment.get());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Envio não encontrado: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Busca um envio pelo ID do pedido
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentResponseDto> getShipmentByOrderId(@PathVariable Long orderId) {
        logger.info("Requisição para buscar envio por Order ID: {}", orderId);
        
        Optional<Shipment> shipment = shipmentUseCases.findShipmentByOrderId(orderId);
        
        if (shipment.isPresent()) {
            ShipmentResponseDto response = webMapper.toResponseDto(shipment.get());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Envio não encontrado para Order ID: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Lista envios por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentResponseDto>> getShipmentsByStatus(@PathVariable ShipmentStatus status) {
        logger.info("Requisição para listar envios por status: {}", status);
        
        List<Shipment> shipments = shipmentUseCases.findShipmentsByStatus(status);
        List<ShipmentResponseDto> response = webMapper.toResponseDtoList(shipments);
        
        logger.info("Retornando {} envios com status {}", response.size(), status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lista envios por email do cliente
     */
    @GetMapping("/customer/{customerEmail}")
    public ResponseEntity<List<ShipmentResponseDto>> getShipmentsByCustomerEmail(@PathVariable String customerEmail) {
        logger.info("Requisição para listar envios por email do cliente: {}", customerEmail);
        
        List<Shipment> shipments = shipmentUseCases.findShipmentsByCustomerEmail(customerEmail);
        List<ShipmentResponseDto> response = webMapper.toResponseDtoList(shipments);
        
        logger.info("Retornando {} envios para cliente {}", response.size(), customerEmail);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Atualiza o status de um envio
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ShipmentResponseDto> updateShipmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequestDto request) {
        
        logger.info("Requisição para atualizar status do envio {} para {}", id, request.getStatus());
        
        try {
            Shipment updatedShipment = shipmentUseCases.updateShipmentStatus(id, request.getStatus());
            ShipmentResponseDto response = webMapper.toResponseDto(updatedShipment);
            
            logger.info("Status do envio {} atualizado com sucesso para {}", id, request.getStatus());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar status do envio {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Define o número de rastreamento para um envio
     */
    @PutMapping("/{id}/tracking")
    public ResponseEntity<ShipmentResponseDto> setTrackingNumber(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        
        logger.info("Requisição para definir número de rastreamento do envio {}: {}", id, trackingNumber);
        
        try {
            Shipment updatedShipment = shipmentUseCases.setTrackingNumber(id, trackingNumber);
            ShipmentResponseDto response = webMapper.toResponseDto(updatedShipment);
            
            logger.info("Número de rastreamento do envio {} definido com sucesso: {}", id, trackingNumber);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao definir número de rastreamento do envio {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Logistics Service is running");
    }
    
    /**
     * Tratamento global de exceções
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("Erro inesperado no controller: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
    }
}