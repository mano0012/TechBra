package com.techbra.logistics.infrastructure.persistence.mapper;

import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.infrastructure.persistence.entity.ShipmentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShipmentMapper {
    
    /**
     * Converte de entidade JPA para modelo de domínio
     */
    public Shipment toDomain(ShipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Shipment shipment = new Shipment(
            entity.getOrderId(),
            entity.getCustomerName(),
            entity.getCustomerEmail(),
            entity.getDeliveryAddress(),
            entity.getCity(),
            entity.getState(),
            entity.getZipCode(),
            entity.getCountry(),
            entity.getTotalAmount()
        );
        
        // Define os campos que são gerenciados pela entidade
        shipment.setId(entity.getId());
        shipment.setCreatedAt(entity.getCreatedAt());
        shipment.setUpdatedAt(entity.getUpdatedAt());
        
        // Atualiza o status se for diferente do padrão
        if (entity.getStatus() != null && entity.getStatus() != shipment.getStatus()) {
            shipment.updateStatus(entity.getStatus());
        }
        
        // Define campos opcionais
        if (entity.getTrackingNumber() != null) {
            shipment.setTrackingNumber(entity.getTrackingNumber());
        }
        
        if (entity.getEstimatedDeliveryDate() != null) {
            shipment.setEstimatedDeliveryDate(entity.getEstimatedDeliveryDate());
        }
        
        if (entity.getTrackingNotes() != null) {
            shipment.setTrackingNotes(entity.getTrackingNotes());
        }
        
        return shipment;
    }
    
    /**
     * Converte de modelo de domínio para entidade JPA
     */
    public ShipmentEntity toEntity(Shipment shipment) {
        if (shipment == null) {
            return null;
        }
        
        ShipmentEntity entity = new ShipmentEntity(
            shipment.getOrderId(),
            shipment.getCustomerName(),
            shipment.getCustomerEmail(),
            shipment.getDeliveryAddress(),
            shipment.getCity(),
            shipment.getState(),
            shipment.getZipCode(),
            shipment.getCountry(),
            shipment.getTotalAmount(),
            shipment.getStatus()
        );
        
        // Define o ID se existir (para updates)
        if (shipment.getId() != null) {
            entity.setId(shipment.getId());
        }
        
        // Define timestamps se existirem
        if (shipment.getCreatedAt() != null) {
            entity.setCreatedAt(shipment.getCreatedAt());
        }
        
        if (shipment.getUpdatedAt() != null) {
            entity.setUpdatedAt(shipment.getUpdatedAt());
        }
        
        // Define campos opcionais
        if (shipment.getTrackingNumber() != null) {
            entity.setTrackingNumber(shipment.getTrackingNumber());
        }
        
        if (shipment.getEstimatedDeliveryDate() != null) {
            entity.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        }
        
        if (shipment.getTrackingNotes() != null) {
            entity.setTrackingNotes(shipment.getTrackingNotes());
        }
        
        return entity;
    }
    
    /**
     * Converte lista de entidades para lista de modelos de domínio
     */
    public List<Shipment> toDomainList(List<ShipmentEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte lista de modelos de domínio para lista de entidades
     */
    public List<ShipmentEntity> toEntityList(List<Shipment> shipments) {
        if (shipments == null) {
            return List.of();
        }
        
        return shipments.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Atualiza uma entidade existente com dados do modelo de domínio
     */
    public void updateEntity(ShipmentEntity entity, Shipment shipment) {
        if (entity == null || shipment == null) {
            return;
        }
        
        entity.setOrderId(shipment.getOrderId());
        entity.setCustomerName(shipment.getCustomerName());
        entity.setCustomerEmail(shipment.getCustomerEmail());
        entity.setDeliveryAddress(shipment.getDeliveryAddress());
        entity.setCity(shipment.getCity());
        entity.setState(shipment.getState());
        entity.setZipCode(shipment.getZipCode());
        entity.setCountry(shipment.getCountry());
        entity.setTotalAmount(shipment.getTotalAmount());
        entity.setStatus(shipment.getStatus());
        entity.setTrackingNumber(shipment.getTrackingNumber());
        entity.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        
        // updatedAt será gerenciado automaticamente pela anotação @UpdateTimestamp
    }
}