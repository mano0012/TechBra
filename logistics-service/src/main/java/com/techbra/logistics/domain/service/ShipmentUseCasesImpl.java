package com.techbra.logistics.domain.service;

import com.techbra.logistics.domain.events.OrderPaidEvent;
import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.model.ShipmentStatus;
import com.techbra.logistics.domain.ports.in.ShipmentUseCases;
import com.techbra.logistics.domain.ports.out.ShipmentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShipmentUseCasesImpl implements ShipmentUseCases {
    
    private static final Logger logger = LoggerFactory.getLogger(ShipmentUseCasesImpl.class);
    
    private final ShipmentRepositoryPort shipmentRepository;
    
    public ShipmentUseCasesImpl(ShipmentRepositoryPort shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }
    
    @Override
    public Shipment createShipmentFromOrder(OrderPaidEvent orderPaidEvent) {
        logger.info("Criando envio para pedido ID: {}", orderPaidEvent.getOrderId());
        
        // Verifica se já existe um envio para este pedido
        if (shipmentRepository.existsByOrderId(orderPaidEvent.getOrderId())) {
            logger.warn("Envio já existe para o pedido ID: {}", orderPaidEvent.getOrderId());
            throw new IllegalArgumentException("Já existe um envio para o pedido: " + orderPaidEvent.getOrderId());
        }
        
        // Cria o novo envio
        Shipment shipment = new Shipment(
            orderPaidEvent.getOrderId(),
            orderPaidEvent.getCustomerName(),
            orderPaidEvent.getCustomerEmail(),
            orderPaidEvent.getDeliveryAddress(),
            orderPaidEvent.getCity(),
            orderPaidEvent.getState(),
            orderPaidEvent.getZipCode(),
            orderPaidEvent.getCountry(),
            orderPaidEvent.getTotalAmount()
        );
        
        // Gera número de rastreamento único
        String trackingNumber = generateTrackingNumber();
        shipment.setTrackingNumber(trackingNumber);
        
        // Define data estimada de entrega (7 dias úteis)
        shipment.setEstimatedDeliveryDate(calculateEstimatedDeliveryDate());
        
        Shipment savedShipment = shipmentRepository.save(shipment);
        
        logger.info("Envio criado com sucesso. ID: {}, Tracking: {}", 
                   savedShipment.getId(), savedShipment.getTrackingNumber());
        
        return savedShipment;
    }
    
    @Override
    public Optional<Shipment> findShipmentById(Long shipmentId) {
        logger.debug("Buscando envio por ID: {}", shipmentId);
        return shipmentRepository.findById(shipmentId);
    }
    
    @Override
    public Optional<Shipment> findShipmentByOrderId(Long orderId) {
        logger.debug("Buscando envio por Order ID: {}", orderId);
        return shipmentRepository.findByOrderId(orderId);
    }
    
    @Override
    public List<Shipment> findAllShipments() {
        logger.debug("Listando todos os envios");
        return shipmentRepository.findAll();
    }
    
    @Override
    public List<Shipment> findShipmentsByStatus(ShipmentStatus status) {
        logger.debug("Buscando envios por status: {}", status);
        return shipmentRepository.findByStatus(status);
    }
    
    @Override
    public Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        logger.info("Atualizando status do envio ID: {} para {}", shipmentId, newStatus);
        
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Envio não encontrado: " + shipmentId));
        
        // Valida se a transição de status é permitida
        if (!shipment.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                String.format("Transição de status inválida: %s -> %s", 
                             shipment.getStatus(), newStatus));
        }
        
        shipment.updateStatus(newStatus);
        Shipment updatedShipment = shipmentRepository.save(shipment);
        
        logger.info("Status do envio atualizado com sucesso. ID: {}, Novo status: {}", 
                   shipmentId, newStatus);
        
        return updatedShipment;
    }
    
    @Override
    public Shipment setTrackingNumber(Long shipmentId, String trackingNumber) {
        logger.info("Definindo número de rastreamento para envio ID: {}", shipmentId);
        
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Envio não encontrado: " + shipmentId));
        
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Número de rastreamento não pode ser vazio");
        }
        
        shipment.setTrackingNumber(trackingNumber.trim());
        Shipment updatedShipment = shipmentRepository.save(shipment);
        
        logger.info("Número de rastreamento definido com sucesso. ID: {}, Tracking: {}", 
                   shipmentId, trackingNumber);
        
        return updatedShipment;
    }
    
    @Override
    public List<Shipment> findShipmentsByCustomerEmail(String customerEmail) {
        logger.debug("Buscando envios por email do cliente: {}", customerEmail);
        return shipmentRepository.findByCustomerEmail(customerEmail);
    }
    
    private String generateTrackingNumber() {
        // Gera um número de rastreamento único no formato: LOG-YYYYMMDD-XXXXXX
        String datePrefix = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("LOG-%s-%s", datePrefix, randomSuffix);
    }
    
    private LocalDateTime calculateEstimatedDeliveryDate() {
        // Calcula data estimada de entrega (7 dias úteis a partir de agora)
        LocalDateTime now = LocalDateTime.now();
        int businessDays = 7;
        int addedDays = 0;
        
        while (businessDays > 0) {
            addedDays++;
            LocalDateTime testDate = now.plusDays(addedDays);
            // Considera apenas dias úteis (segunda a sexta)
            if (testDate.getDayOfWeek().getValue() <= 5) {
                businessDays--;
            }
        }
        
        return now.plusDays(addedDays);
    }
    
    @Override
    public Shipment createShipment(Shipment shipment) {
        logger.info("Criando novo envio para pedido ID: {}", shipment.getOrderId());
        
        // Define valores padrão se não estiverem definidos
        if (shipment.getCreatedAt() == null) {
            shipment.setCreatedAt(LocalDateTime.now());
        }
        if (shipment.getUpdatedAt() == null) {
            shipment.setUpdatedAt(LocalDateTime.now());
        }
        if (shipment.getStatus() == null) {
            shipment.setStatus(ShipmentStatus.PENDING);
        }
        
        return shipmentRepository.save(shipment);
    }
    
    @Override
    public Shipment updateShipment(Shipment shipment) {
        logger.info("Atualizando envio ID: {}", shipment.getId());
        
        shipment.setUpdatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }
    
    @Override
    public List<Shipment> findByOrderId(Long orderId) {
        logger.info("Buscando envios para pedido ID: {}", orderId);
        
        return shipmentRepository.findAllByOrderId(orderId);
    }
}