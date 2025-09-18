package com.techbra.order.service;

import com.techbra.order.domain.event.OrderEvent;
import com.techbra.order.domain.event.OrderPayload;
import com.techbra.order.domain.event.OrderPayload.OrderItemPayload;
import com.techbra.order.domain.event.OrderPayload.AddressPayload;
import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderItem;
import com.techbra.order.domain.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável por publicar eventos de pedidos no Kafka
 * 
 * Esta classe gerencia a publicação de eventos relacionados aos pedidos
 * no tópico billing-events do Kafka.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
@Service
public class OrderEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);

    @Value("${kafka.topics.billing-events:billing-events}")
    private String billingEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publica um evento de criação de pedido
     * 
     * @param order o pedido que foi criado
     */
    public void publishOrderCreatedEvent(Order order) {
        try {
            OrderEvent event = createOrderEvent("ORDER_CREATED", order);
            publishEvent(event);
            logger.info("Evento de criação de pedido publicado com sucesso. OrderId: {}, EventId: {}", 
                       order.getId(), event.getEventId());
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de criação de pedido. OrderId: {}", order.getId(), e);
            throw new RuntimeException("Falha ao publicar evento de pedido", e);
        }
    }

    /**
     * Publica um evento de atualização de pedido
     * 
     * @param order o pedido que foi atualizado
     */
    public void publishOrderUpdatedEvent(Order order) {
        try {
            OrderEvent event = createOrderEvent("ORDER_UPDATED", order);
            publishEvent(event);
            logger.info("Evento de atualização de pedido publicado com sucesso. OrderId: {}, EventId: {}", 
                       order.getId(), event.getEventId());
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de atualização de pedido. OrderId: {}", order.getId(), e);
            throw new RuntimeException("Falha ao publicar evento de pedido", e);
        }
    }

    /**
     * Cria um evento de pedido a partir dos dados do pedido
     * 
     * @param eventType tipo do evento (ORDER_CREATED, ORDER_UPDATED, etc.)
     * @param order pedido fonte dos dados
     * @return evento criado
     */
    private OrderEvent createOrderEvent(String eventType, Order order) {
        // Converter itens do pedido
        List<OrderItemPayload> itemPayloads = order.getItems().stream()
                .map(this::convertToItemPayload)
                .collect(Collectors.toList());

        // Converter endereços
        AddressPayload shippingAddress = convertToAddressPayload(order.getShippingAddress());
        AddressPayload billingAddress = convertToAddressPayload(order.getBillingAddress());

        // Criar payload
        OrderPayload payload = new OrderPayload(
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemPayloads,
                shippingAddress,
                billingAddress,
                order.getPaymentMethod(),
                order.getNotes()
        );

        // Criar evento
        return new OrderEvent(
                eventType,
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus().toString(),
                payload
        );
    }

    /**
     * Converte um item do pedido para o payload do evento
     * 
     * @param item item do pedido
     * @return payload do item
     */
    private OrderItemPayload convertToItemPayload(OrderItem item) {
        return new OrderItemPayload(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }

    /**
     * Converte um endereço para o payload do evento
     * 
     * @param address endereço
     * @return payload do endereço
     */
    private AddressPayload convertToAddressPayload(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressPayload(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    /**
     * Publica o evento no tópico do Kafka
     * 
     * @param event evento a ser publicado
     */
    private void publishEvent(OrderEvent event) {
        String key = event.getOrderId().toString();
        
        ListenableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(billingEventsTopic, key, event);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                logger.debug("Evento enviado com sucesso para o tópico {} com key {} e offset {}",
                           billingEventsTopic, key, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Falha ao enviar evento para o tópico {} com key {}", 
                           billingEventsTopic, key, ex);
            }
        });
    }
}