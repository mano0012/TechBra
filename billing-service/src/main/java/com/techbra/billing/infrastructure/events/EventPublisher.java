package com.techbra.billing.infrastructure.events;

import com.techbra.billing.domain.events.BillPaidEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Publisher de eventos que simula o envio para um sistema de mensageria.
 * Futuramente será integrado com Kafka.
 */
@Component
public class EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    
    /**
     * Publica um evento de fatura paga.
     * Por enquanto apenas simula o envio logando o evento.
     * 
     * @param event o evento de fatura paga
     */
    public void publishBillPaidEvent(BillPaidEvent event) {
        logger.info("📧 Simulando envio de evento: {}", event);
        logger.info("🚀 Evento BillPaidEvent seria enviado para Kafka - Bill ID: {}, Amount: {}", 
                   event.getBillId(), event.getAmount());
        
        // Aqui futuramente será implementada a integração com Kafka
        // kafkaTemplate.send("bill-paid-topic", event);
    }
}