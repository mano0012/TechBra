package com.techbra.logistics.domain.ports.in;

import com.techbra.logistics.domain.events.OrderPaidEvent;
import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.model.ShipmentStatus;

import java.util.List;
import java.util.Optional;

public interface ShipmentUseCases {
    
    /**
     * Cria um novo envio baseado no evento de pedido pago
     * @param orderPaidEvent evento contendo dados do pedido pago
     * @return o envio criado
     */
    Shipment createShipmentFromOrder(OrderPaidEvent orderPaidEvent);
    
    /**
     * Busca um envio pelo ID
     * @param shipmentId ID do envio
     * @return envio encontrado ou vazio
     */
    Optional<Shipment> findShipmentById(Long shipmentId);
    
    /**
     * Busca um envio pelo ID do pedido
     * @param orderId ID do pedido
     * @return envio encontrado ou vazio
     */
    Optional<Shipment> findShipmentByOrderId(Long orderId);
    
    /**
     * Lista todos os envios
     * @return lista de envios
     */
    List<Shipment> findAllShipments();
    
    /**
     * Lista envios por status
     * @param status status do envio
     * @return lista de envios com o status especificado
     */
    List<Shipment> findShipmentsByStatus(ShipmentStatus status);
    
    /**
     * Atualiza o status de um envio
     * @param shipmentId ID do envio
     * @param newStatus novo status
     * @return envio atualizado
     * @throws IllegalArgumentException se o envio não for encontrado ou transição inválida
     */
    Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus);
    
    /**
     * Define o número de rastreamento para um envio
     * @param shipmentId ID do envio
     * @param trackingNumber número de rastreamento
     * @return envio atualizado
     * @throws IllegalArgumentException se o envio não for encontrado
     */
    Shipment setTrackingNumber(Long shipmentId, String trackingNumber);
    
    /**
     * Busca envios por email do cliente
     * @param customerEmail email do cliente
     * @return lista de envios do cliente
     */
    List<Shipment> findShipmentsByCustomerEmail(String customerEmail);
}