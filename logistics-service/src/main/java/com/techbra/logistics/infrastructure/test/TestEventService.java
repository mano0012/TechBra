package com.techbra.logistics.infrastructure.test;

import com.techbra.logistics.domain.events.OrderPaidEvent;
import com.techbra.logistics.infrastructure.messaging.OrderPaidEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Serviço para testes manuais de processamento de eventos
 * Simula o recebimento de eventos OrderPaidEvent
 */
@Service
public class TestEventService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestEventService.class);
    
    @Autowired
    private OrderPaidEventListener eventListener;
    
    /**
     * Simula o processamento de um evento de pedido pago
     */
    public void simulateOrderPaidEvent(Long orderId, String customerName, String customerEmail,
                                      String deliveryAddress, String city, String state,
                                      String zipCode, String country, BigDecimal totalAmount) {
        
        logger.info("Simulando evento de pedido pago para orderId: {}", orderId);
        
        OrderPaidEvent event = new OrderPaidEvent(
            orderId,
            customerName,
            customerEmail,
            deliveryAddress,
            city,
            state,
            zipCode,
            country,
            totalAmount,
            LocalDateTime.now()
        );
        
        try {
            eventListener.handleOrderPaidEvent(event);
            logger.info("Evento processado com sucesso para orderId: {}", orderId);
        } catch (Exception e) {
            logger.error("Erro ao processar evento para orderId: {}", orderId, e);
            throw e;
        }
    }
    
    /**
     * Cria um evento de teste com dados padrão
     */
    public void createTestShipment() {
        simulateOrderPaidEvent(
            1001L,
            "João Silva",
            "joao.silva@email.com",
            "Rua das Flores, 123, Apt 45",
            "São Paulo",
            "SP",
            "01234-567",
            "Brasil",
            new BigDecimal("299.99")
        );
    }
    
    /**
     * Cria múltiplos eventos de teste
     */
    public void createMultipleTestShipments() {
        // Evento 1
        simulateOrderPaidEvent(
            2001L,
            "Maria Santos",
            "maria.santos@email.com",
            "Av. Paulista, 1000",
            "São Paulo",
            "SP",
            "01310-100",
            "Brasil",
            new BigDecimal("150.00")
        );
        
        // Evento 2
        simulateOrderPaidEvent(
            2002L,
            "Pedro Oliveira",
            "pedro.oliveira@email.com",
            "Rua Copacabana, 500",
            "Rio de Janeiro",
            "RJ",
            "22070-001",
            "Brasil",
            new BigDecimal("89.90")
        );
        
        // Evento 3
        simulateOrderPaidEvent(
            2003L,
            "Ana Costa",
            "ana.costa@email.com",
            "Rua da Praia, 200",
            "Salvador",
            "BA",
            "40070-110",
            "Brasil",
            new BigDecimal("320.50")
        );
    }
}