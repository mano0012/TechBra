package com.techbra.billing.service;

import com.techbra.billing.domain.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Listener responsável por consumir eventos de pedidos do tópico billing-events
 */
@Service
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    @Autowired
    private BillingProcessingService billingProcessingService;

    /**
     * Consome eventos de criação de pedidos do tópico billing-events
     */
    @KafkaListener(
        topics = "billing-events",
        groupId = "billing-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderEvent(
            @Payload OrderEvent orderEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Recebido evento de pedido: eventId={}, eventType={}, orderId={}, topic={}, partition={}, offset={}", 
                       orderEvent.getEventId(), orderEvent.getEventType(), orderEvent.getOrderId(), topic, partition, offset);

            // Processa o evento baseado no tipo
            switch (orderEvent.getEventType()) {
                case "ORDER_CREATED":
                    handleOrderCreated(orderEvent);
                    break;
                case "ORDER_UPDATED":
                    handleOrderUpdated(orderEvent);
                    break;
                case "ORDER_CANCELLED":
                    handleOrderCancelled(orderEvent);
                    break;
                default:
                    logger.warn("Tipo de evento não reconhecido: {}", orderEvent.getEventType());
            }

            // Confirma o processamento da mensagem
            acknowledgment.acknowledge();
            logger.info("Evento processado com sucesso: eventId={}", orderEvent.getEventId());

        } catch (Exception e) {
            logger.error("Erro ao processar evento de pedido: eventId={}, orderId={}, erro={}", 
                        orderEvent.getEventId(), orderEvent.getOrderId(), e.getMessage(), e);
            
            // Em caso de erro, não confirma a mensagem para reprocessamento
            // O Kafka irá reenviar a mensagem após o timeout
            throw new RuntimeException("Falha no processamento do evento", e);
        }
    }

    /**
     * Processa evento de criação de pedido
     */
    private void handleOrderCreated(OrderEvent orderEvent) {
        logger.info("Processando criação de pedido: orderId={}, customerId={}, totalAmount={}", 
                   orderEvent.getOrderId(), orderEvent.getCustomerId(), orderEvent.getTotalAmount());
        
        try {
            // Delega o processamento para o serviço de cobrança
            billingProcessingService.processOrderCreated(orderEvent);
            
            logger.info("Cobrança criada com sucesso para o pedido: orderId={}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            logger.error("Erro ao criar cobrança para o pedido: orderId={}, erro={}", 
                        orderEvent.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Processa evento de atualização de pedido
     */
    private void handleOrderUpdated(OrderEvent orderEvent) {
        logger.info("Processando atualização de pedido: orderId={}, status={}", 
                   orderEvent.getOrderId(), orderEvent.getStatus());
        
        try {
            // Delega o processamento para o serviço de cobrança
            billingProcessingService.processOrderUpdated(orderEvent);
            
            logger.info("Cobrança atualizada com sucesso para o pedido: orderId={}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar cobrança para o pedido: orderId={}, erro={}", 
                        orderEvent.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Processa evento de cancelamento de pedido
     */
    private void handleOrderCancelled(OrderEvent orderEvent) {
        logger.info("Processando cancelamento de pedido: orderId={}", orderEvent.getOrderId());
        
        try {
            // Delega o processamento para o serviço de cobrança
            billingProcessingService.processOrderCancelled(orderEvent);
            
            logger.info("Cobrança cancelada com sucesso para o pedido: orderId={}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            logger.error("Erro ao cancelar cobrança para o pedido: orderId={}, erro={}", 
                        orderEvent.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }
}