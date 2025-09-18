package com.techbra.logistics.infrastructure.web.mapper;

import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.infrastructure.web.dto.ShipmentResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShipmentWebMapper {
    
    /**
     * Converte de modelo de domínio para DTO de resposta
     */
    public ShipmentResponseDto toResponseDto(Shipment shipment) {
        if (shipment == null) {
            return null;
        }
        
        return new ShipmentResponseDto(
            shipment.getId(),
            shipment.getOrderId(),
            shipment.getCustomerName(),
            shipment.getCustomerEmail(),
            shipment.getDeliveryAddress(),
            shipment.getCity(),
            shipment.getState(),
            shipment.getZipCode(),
            shipment.getCountry(),
            shipment.getTotalAmount(),
            shipment.getStatus(),
            shipment.getCreatedAt(),
            shipment.getUpdatedAt(),
            shipment.getEstimatedDeliveryDate(),
            shipment.getTrackingNumber()
        );
    }
    
    /**
     * Converte lista de modelos de domínio para lista de DTOs de resposta
     */
    public List<ShipmentResponseDto> toResponseDtoList(List<Shipment> shipments) {
        if (shipments == null) {
            return List.of();
        }
        
        return shipments.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}