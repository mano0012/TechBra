package com.techbra.billing.service;

import com.techbra.billing.domain.event.LogisticsEvent;
import com.techbra.billing.domain.event.LogisticsPayload;
import com.techbra.billing.domain.model.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço responsável por publicar eventos de cobrança para o logistics-service
 */
@Service
public class LogisticsEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(LogisticsEventPublisher.class);

    @Autowired
    private KafkaTemplate<String, LogisticsEvent> logisticsEventKafkaTemplate;

    @Value("${app.kafka.topics.logistics-events:logistics-events}")
    private String logisticsEventsTopic;

    /**
     * Publica evento de cobrança criada
     */
    public void publishBillCreatedEvent(Bill bill) {
        try {
            LogisticsEvent event = createLogisticsEvent("BILL_CREATED", bill, "Nova cobrança criada e aguardando processamento");
            publishEvent(event, bill.getOrderId().toString());
            
            logger.info("Evento BILL_CREATED publicado: billId={}, orderId={}", bill.getId(), bill.getOrderId());
        } catch (Exception e) {
            logger.error("Erro ao publicar evento BILL_CREATED: billId={}, orderId={}", bill.getId(), bill.getOrderId(), e);
        }
    }

    /**
     * Publica evento de cobrança atualizada
     */
    public void publishBillUpdatedEvent(Bill bill) {
        try {
            LogisticsEvent event = createLogisticsEvent("BILL_UPDATED", bill, "Cobrança atualizada com novos dados");
            publishEvent(event, bill.getOrderId().toString());
            
            logger.info("Evento BILL_UPDATED publicado: billId={}, orderId={}", bill.getId(), bill.getOrderId());
        } catch (Exception e) {
            logger.error("Erro ao publicar evento BILL_UPDATED: billId={}, orderId={}", bill.getId(), bill.getOrderId(), e);
        }
    }

    /**
     * Publica evento de cobrança paga
     */
    public void publishBillPaidEvent(Bill bill) {
        try {
            LogisticsEvent event = createLogisticsEvent("BILL_PAID", bill, "Cobrança paga - pedido liberado para logística");
            publishEvent(event, bill.getOrderId().toString());
            
            logger.info("Evento BILL_PAID publicado: billId={}, orderId={}", bill.getId(), bill.getOrderId());
        } catch (Exception e) {
            logger.error("Erro ao publicar evento BILL_PAID: billId={}, orderId={}", bill.getId(), bill.getOrderId(), e);
        }
    }

    /**
     * Publica evento de cobrança cancelada
     */
    public void publishBillCancelledEvent(Bill bill) {
        try {
            LogisticsEvent event = createLogisticsEvent("BILL_CANCELLED", bill, "Cobrança cancelada - pedido não será processado");
            publishEvent(event, bill.getOrderId().toString());
            
            logger.info("Evento BILL_CANCELLED publicado: billId={}, orderId={}", bill.getId(), bill.getOrderId());
        } catch (Exception e) {
            logger.error("Erro ao publicar evento BILL_CANCELLED: billId={}, orderId={}", bill.getId(), bill.getOrderId(), e);
        }
    }

    /**
     * Cria um evento de logística a partir de uma cobrança
     */
    private LogisticsEvent createLogisticsEvent(String eventType, Bill bill, String processingNotes) {
        String eventId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();

        // Cria o payload com informações detalhadas
        LogisticsPayload payload = new LogisticsPayload(
            bill.getDescription(),
            bill.getDueDate(),
            bill.getCreatedAt(),
            bill.getPaidAt(),
            processingNotes
        );

        // Cria o evento principal
        LogisticsEvent event = new LogisticsEvent(
            eventId,
            eventType,
            timestamp,
            bill.getId(),
            bill.getOrderId(),
            null, // customerId não está disponível na entidade Bill atual
            bill.getAmount(),
            bill.getStatus().toString(),
            payload
        );

        return event;
    }

    /**
     * Publica o evento no tópico Kafka
     */
    private void publishEvent(LogisticsEvent event, String key) {
        CompletableFuture<SendResult<String, LogisticsEvent>> future = 
            logisticsEventKafkaTemplate.send(logisticsEventsTopic, key, event);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.debug("Evento publicado com sucesso: eventId={}, topic={}, partition={}, offset={}", 
                           event.getEventId(), result.getRecordMetadata().topic(), 
                           result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                logger.error("Falha ao publicar evento: eventId={}, topic={}", 
                           event.getEventId(), logisticsEventsTopic, exception);
            }
        });
    }
}