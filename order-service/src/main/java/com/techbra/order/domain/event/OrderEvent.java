package com.techbra.order.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de pedido para publicação no Kafka
 * 
 * Esta classe representa os dados do pedido que serão enviados
 * para o tópico billing-events quando um pedido for criado.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class OrderEvent {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("orderId")
    private UUID orderId;

    @JsonProperty("customerId")
    private UUID customerId;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payload")
    private OrderPayload payload;

    // Construtor padrão
    public OrderEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    // Construtor com parâmetros
    public OrderEvent(String eventType, UUID orderId, UUID customerId, 
                     BigDecimal totalAmount, String status, OrderPayload payload) {
        this();
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.payload = payload;
    }

    // Getters e Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderPayload getPayload() {
        return payload;
    }

    public void setPayload(OrderPayload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", payload=" + payload +
                '}';
    }
}