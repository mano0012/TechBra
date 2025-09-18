package com.techbra.billing.service;

import com.techbra.billing.domain.event.OrderEvent;
import com.techbra.billing.domain.model.Bill;
import com.techbra.billing.domain.model.BillStatus;
import com.techbra.billing.domain.ports.out.BillRepositoryPort;
import com.techbra.billing.service.LogisticsEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço responsável por processar eventos de pedidos e gerar cobranças
 */
@Service
@Transactional
public class BillingProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingProcessingService.class);

    @Autowired
    private BillRepositoryPort billRepository;

    @Autowired
    private LogisticsEventPublisher logisticsEventPublisher;

    /**
     * Processa evento de criação de pedido e gera cobrança
     */
    public void processOrderCreated(OrderEvent orderEvent) {
        logger.info("Iniciando processamento de cobrança para pedido criado: orderId={}", orderEvent.getOrderId());

        try {
            // Verifica se já existe uma cobrança para este pedido
            Optional<Bill> existingBill = billRepository.findByOrderId(UUID.fromString(orderEvent.getOrderId().toString()));
            if (existingBill.isPresent()) {
                logger.warn("Cobrança já existe para o pedido: orderId={}, billId={}", 
                           orderEvent.getOrderId(), existingBill.get().getId());
                return;
            }

            // Cria nova cobrança
            Bill bill = createBillFromOrderEvent(orderEvent);
            
            // Salva a cobrança
            Bill savedBill = billRepository.save(bill);
            
            // Publica evento de cobrança criada no logistics-events
            logisticsEventPublisher.publishBillCreatedEvent(savedBill);
            
            logger.info("Cobrança criada com sucesso: billId={}, orderId={}, amount={}", 
                       savedBill.getId(), orderEvent.getOrderId(), orderEvent.getTotalAmount());

            // Aqui poderia ser adicionada lógica adicional como:
            // - Envio de notificação para o cliente
            // - Integração com gateway de pagamento
            // - Geração de fatura
            
        } catch (Exception e) {
            logger.error("Erro ao processar criação de cobrança: orderId={}, erro={}", 
                        orderEvent.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Falha ao criar cobrança para o pedido: " + orderEvent.getOrderId(), e);
        }
    }

    /**
     * Processa evento de atualização de pedido
     */
    public void processOrderUpdated(OrderEvent orderEvent) {
        logger.info("Iniciando processamento de atualização de cobrança: orderId={}", orderEvent.getOrderId());

        try {
            // Busca a cobrança existente
            Optional<Bill> existingBill = billRepository.findByOrderId(UUID.fromString(orderEvent.getOrderId().toString()));
            
            if (existingBill.isEmpty()) {
                logger.warn("Cobrança não encontrada para o pedido: orderId={}", orderEvent.getOrderId());
                // Cria nova cobrança se não existir
                processOrderCreated(orderEvent);
                return;
            }

            Bill bill = existingBill.get();
            
            // Atualiza os dados da cobrança
            updateBillFromOrderEvent(bill, orderEvent);
            
            // Salva as alterações
            Bill updatedBill = billRepository.save(bill);
            
            // Publica evento de cobrança atualizada no logistics-events
            logisticsEventPublisher.publishBillUpdatedEvent(updatedBill);
            
            logger.info("Cobrança atualizada com sucesso: billId={}, orderId={}", 
                       updatedBill.getId(), orderEvent.getOrderId());
            
        } catch (Exception e) {
            logger.error("Erro ao processar atualização de cobrança: orderId={}, erro={}", 
                        orderEvent.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Falha ao atualizar cobrança para o pedido: " + orderEvent.getOrderId(), e);
        }
    }

    /**
     * Processa evento de cancelamento de pedido
     */
    public void processOrderCancelled(OrderEvent orderEvent) {
        logger.info("Iniciando processamento de cancelamento de cobrança: orderId={}", orderEvent.getOrderId());

        try {
            // Busca a cobrança existente
            Optional<Bill> existingBill = billRepository.findByOrderId(UUID.fromString(orderEvent.getOrderId().toString()));
            
            if (existingBill.isEmpty()) {
                logger.warn("Cobrança não encontrada para cancelamento: orderId={}", orderEvent.getOrderId());
                return;
            }

            Bill bill = existingBill.get();
            
            // Cancela a cobrança
            bill.setStatus(BillStatus.CANCELLED);
            bill.setCancelledAt(LocalDateTime.now());
            bill.setCancellationReason("Pedido cancelado");
            
            // Salva as alterações
            Bill cancelledBill = billRepository.save(bill);
            
            // Publica evento de cobrança cancelada no logistics-events
            logisticsEventPublisher.publishBillCancelledEvent(cancelledBill);
            
            logger.info("Cobrança cancelada com sucesso: billId={}, orderId={}", 
                       cancelledBill.getId(), orderEvent.getOrderId());

            // Aqui poderia ser adicionada lógica adicional como:
            // - Processamento de estorno se já foi pago
            // - Notificação de cancelamento
            
        } catch (Exception e) {
            logger.error("Erro ao processar cancelamento de cobrança: orderId={}, erro={}", 
                        orderEvent.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Falha ao cancelar cobrança para o pedido: " + orderEvent.getOrderId(), e);
        }
    }

    /**
     * Cria uma nova cobrança a partir do evento de pedido
     */
    private Bill createBillFromOrderEvent(OrderEvent orderEvent) {
        // Dados básicos
        UUID orderId = UUID.fromString(orderEvent.getOrderId().toString());
        String description;
        BigDecimal amount = orderEvent.getTotalAmount();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(30);
        
        // Dados do payload se disponível
        if (orderEvent.getPayload() != null) {
            description = "Cobrança para pedido: " + orderEvent.getPayload().getOrderNumber();
        } else {
            description = "Cobrança para pedido ID: " + orderEvent.getOrderId();
        }
        
        Bill bill = new Bill(orderId, description, amount, dueDate);
        bill.setStatus(BillStatus.PENDING);
        bill.setCreatedAt(LocalDateTime.now());
        
        return bill;
    }

    /**
     * Atualiza uma cobrança existente com dados do evento
     */
    private void updateBillFromOrderEvent(Bill bill, OrderEvent orderEvent) {
        // Atualiza valor se mudou
        if (orderEvent.getTotalAmount() != null && 
            orderEvent.getTotalAmount().compareTo(bill.getAmount()) != 0) {
            bill.setAmount(orderEvent.getTotalAmount());
        }
        
        // Atualiza timestamp de modificação
        bill.setUpdatedAt(LocalDateTime.now());
        
        logger.debug("Cobrança atualizada: billId={}, newAmount={}", bill.getId(), bill.getAmount());
    }
}