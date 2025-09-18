package com.techbra.logistics.infrastructure.messaging;

import com.techbra.logistics.domain.events.OrderPaidEvent;
import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.ports.in.ShipmentUseCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos de pedidos pagos.
 * Esta classe está preparada para integração com Kafka no futuro.
 * Por enquanto, fornece métodos para processamento manual de eventos.
 */
@Component
public class OrderPaidEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderPaidEventListener.class);
    
    private final ShipmentUseCases shipmentUseCases;
    
    public OrderPaidEventListener(ShipmentUseCases shipmentUseCases) {
        this.shipmentUseCases = shipmentUseCases;
    }
    
    /**
     * Processa um evento de pedido pago.
     * Este método será usado pelo listener do Kafka quando a integração for implementada.
     * 
     * Futura anotação Kafka:
     * @KafkaListener(topics = "order-paid-events", groupId = "logistics-service")
     */
    public void handleOrderPaidEvent(OrderPaidEvent event) {
        logger.info("Recebido evento de pedido pago: {}", event);
        
        try {
            // Valida o evento
            validateEvent(event);
            
            // Cria o envio baseado no evento
            Shipment shipment = shipmentUseCases.createShipmentFromOrder(event);
            
            logger.info("Envio criado com sucesso para pedido {}: ID {}, Tracking: {}", 
                       event.getOrderId(), shipment.getId(), shipment.getTrackingNumber());
            
            // Aqui poderia ser enviado um evento de confirmação ou notificação
            // publishShipmentCreatedEvent(shipment);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Erro de validação ao processar evento de pedido pago: {}", e.getMessage());
            // Em um cenário real, poderia enviar para uma DLQ (Dead Letter Queue)
            
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar evento de pedido pago: {}", e.getMessage(), e);
            // Em um cenário real, poderia implementar retry ou enviar para DLQ
            throw e; // Re-throw para que o Kafka possa fazer retry
        }
    }
    
    /**
     * Método público para processamento manual de eventos (para testes ou simulação)
     */
    public Shipment processOrderPaidEvent(OrderPaidEvent event) {
        logger.info("Processamento manual de evento de pedido pago: {}", event.getOrderId());
        
        validateEvent(event);
        return shipmentUseCases.createShipmentFromOrder(event);
    }
    
    /**
     * Valida se o evento contém todos os dados necessários
     */
    private void validateEvent(OrderPaidEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Evento não pode ser nulo");
        }
        
        if (event.getOrderId() == null) {
            throw new IllegalArgumentException("ID do pedido é obrigatório");
        }
        
        if (event.getCustomerName() == null || event.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }
        
        if (event.getCustomerEmail() == null || event.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do cliente é obrigatório");
        }
        
        if (event.getDeliveryAddress() == null || event.getDeliveryAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço de entrega é obrigatório");
        }
        
        if (event.getCity() == null || event.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        
        if (event.getState() == null || event.getState().trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        
        if (event.getZipCode() == null || event.getZipCode().trim().isEmpty()) {
            throw new IllegalArgumentException("CEP é obrigatório");
        }
        
        if (event.getCountry() == null || event.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("País é obrigatório");
        }
        
        if (event.getTotalAmount() == null || event.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero");
        }
        
        if (event.getPaidAt() == null) {
            throw new IllegalArgumentException("Data de pagamento é obrigatória");
        }
        
        logger.debug("Evento validado com sucesso: {}", event.getOrderId());
    }
    
    /**
     * Método preparado para futura publicação de eventos de envio criado
     */
    private void publishShipmentCreatedEvent(Shipment shipment) {
        // Implementação futura para publicar evento de envio criado
        logger.debug("Evento de envio criado seria publicado aqui para: {}", shipment.getId());
    }
}