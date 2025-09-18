package com.techbra.logistics.infrastructure.messaging;

import com.techbra.logistics.domain.events.LogisticsEvent;
import com.techbra.logistics.domain.model.Shipment;
import com.techbra.logistics.domain.model.ShipmentStatus;
import com.techbra.logistics.domain.ports.in.ShipmentUseCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Listener para processar eventos de logística recebidos do Kafka
 */
@Component
public class LogisticsEventListener {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsEventListener.class);

    @Autowired
    private ShipmentUseCases shipmentUseCases;

    /**
     * Processa eventos de cobrança recebidos do tópico logistics-events
     */
    @KafkaListener(
        topics = "logistics-events",
        groupId = "logistics-service-group",
        containerFactory = "logisticsEventKafkaListenerContainerFactory"
    )
    public void handleLogisticsEvent(
            @Payload LogisticsEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        logger.info("Recebido evento de logística: eventId={}, eventType={}, billId={}, orderId={}, topic={}, partition={}, offset={}",
                event.getEventId(), event.getEventType(), event.getBillId(), event.getOrderId(), topic, partition, offset);

        try {
            processLogisticsEvent(event);
            
            // Confirma o processamento do evento
            acknowledgment.acknowledge();
            
            logger.info("Evento processado com sucesso: eventId={}, eventType={}", 
                    event.getEventId(), event.getEventType());
                    
        } catch (Exception e) {
            logger.error("Erro ao processar evento de logística: eventId={}, eventType={}, erro={}", 
                    event.getEventId(), event.getEventType(), e.getMessage(), e);
            
            // Em caso de erro, não confirma o evento para reprocessamento
            // Pode implementar lógica de retry ou dead letter queue aqui
            throw e;
        }
    }

    /**
     * Processa o evento de logística baseado no tipo
     */
    private void processLogisticsEvent(LogisticsEvent event) {
        switch (event.getEventType()) {
            case "BILL_CREATED":
                handleBillCreated(event);
                break;
            case "BILL_UPDATED":
                handleBillUpdated(event);
                break;
            case "BILL_CANCELLED":
                handleBillCancelled(event);
                break;
            default:
                logger.warn("Tipo de evento não reconhecido: {}", event.getEventType());
        }
    }

    /**
     * Processa evento de cobrança criada
     */
    private void handleBillCreated(LogisticsEvent event) {
        logger.info("Processando criação de cobrança: billId={}, orderId={}, customerId={}", 
                event.getBillId(), event.getOrderId(), event.getCustomerId());

        try {
            // Verifica se já existe um shipment para este pedido
            if (event.getOrderId() != null) {
                // Converte UUID para Long
                Long orderId = Long.valueOf(event.getOrderId().toString().hashCode());
                
                // Cria um novo shipment com status PENDING
                Shipment shipment = new Shipment();
                shipment.setOrderId(orderId);
                shipment.setStatus(ShipmentStatus.PENDING);
                shipment.setCreatedAt(LocalDateTime.now());
                shipment.setUpdatedAt(LocalDateTime.now());
                
                // Adiciona informações da cobrança como observações
                String notes = String.format("Cobrança criada - ID: %s, Valor: %s, Status: %s", 
                        event.getBillId(), event.getAmount(), event.getBillStatus());
                shipment.setTrackingNotes(notes);

                shipmentUseCases.createShipment(shipment);
                
                logger.info("Shipment criado para pedido: orderId={}, shipmentId={}", 
                        event.getOrderId(), shipment.getId());
            }
        } catch (Exception e) {
            logger.error("Erro ao processar criação de cobrança: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Processa evento de cobrança atualizada
     */
    private void handleBillUpdated(LogisticsEvent event) {
        logger.info("Processando atualização de cobrança: billId={}, orderId={}, status={}", 
                event.getBillId(), event.getOrderId(), event.getBillStatus());

        try {
            if (event.getOrderId() != null) {
                // Converte UUID para Long
                Long orderId = Long.valueOf(event.getOrderId().toString().hashCode());
                
                // Busca shipments relacionados ao pedido
                var shipments = shipmentUseCases.findByOrderId(orderId);
                
                for (Shipment shipment : shipments) {
                    // Atualiza status baseado no status da cobrança
                    if ("PAID".equals(event.getBillStatus())) {
                        shipment.updateStatus(ShipmentStatus.PROCESSING);
                        
                        String notes = (shipment.getTrackingNotes() != null ? shipment.getTrackingNotes() : "") + 
                                String.format(" | Cobrança paga - %s", LocalDateTime.now());
                        shipment.setTrackingNotes(notes);
                        
                    } else if ("OVERDUE".equals(event.getBillStatus())) {
                        shipment.updateStatus(ShipmentStatus.CANCELLED);
                        
                        String notes = (shipment.getTrackingNotes() != null ? shipment.getTrackingNotes() : "") + 
                                String.format(" | Cobrança em atraso - %s", LocalDateTime.now());
                        shipment.setTrackingNotes(notes);
                    }
                    
                    shipment.setUpdatedAt(LocalDateTime.now());
                    shipmentUseCases.updateShipment(shipment);
                    
                    logger.info("Shipment atualizado: shipmentId={}, novoStatus={}", 
                            shipment.getId(), shipment.getStatus());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao processar atualização de cobrança: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Processa evento de cobrança cancelada
     */
    private void handleBillCancelled(LogisticsEvent event) {
        logger.info("Processando cancelamento de cobrança: billId={}, orderId={}", 
                event.getBillId(), event.getOrderId());

        try {
            if (event.getOrderId() != null) {
                // Converte UUID para Long
                Long orderId = Long.valueOf(event.getOrderId().toString().hashCode());
                
                // Busca shipments relacionados ao pedido
                var shipments = shipmentUseCases.findByOrderId(orderId);
                
                for (Shipment shipment : shipments) {
                    // Cancela o shipment
                    shipment.updateStatus(ShipmentStatus.CANCELLED);
                    
                    String notes = (shipment.getTrackingNotes() != null ? shipment.getTrackingNotes() : "") + 
                            String.format(" | Cobrança cancelada - %s", LocalDateTime.now());
                    shipment.setTrackingNotes(notes);
                    
                    shipment.setUpdatedAt(LocalDateTime.now());
                    shipmentUseCases.updateShipment(shipment);
                    
                    logger.info("Shipment cancelado: shipmentId={}", shipment.getId());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao processar cancelamento de cobrança: {}", e.getMessage(), e);
            throw e;
        }
    }
}