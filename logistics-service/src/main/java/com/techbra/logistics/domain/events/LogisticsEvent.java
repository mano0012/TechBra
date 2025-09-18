package com.techbra.logistics.domain.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de logística recebido do billing-service
 */
public class LogisticsEvent {

    @JsonProperty("eventId")
    private UUID eventId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("billId")
    private UUID billId;

    @JsonProperty("orderId")
    private UUID orderId;

    @JsonProperty("customerId")
    private UUID customerId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("billStatus")
    private String billStatus;

    @JsonProperty("payload")
    private LogisticsPayload payload;

    // Construtores
    public LogisticsEvent() {}

    public LogisticsEvent(UUID eventId, String eventType, LocalDateTime timestamp, 
                         UUID billId, UUID orderId, UUID customerId, 
                         BigDecimal amount, String billStatus, LogisticsPayload payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.billId = billId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.billStatus = billStatus;
        this.payload = payload;
    }

    // Getters e Setters
    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
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

    public UUID getBillId() {
        return billId;
    }

    public void setBillId(UUID billId) {
        this.billId = billId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public LogisticsPayload getPayload() {
        return payload;
    }

    public void setPayload(LogisticsPayload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "LogisticsEvent{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", billId=" + billId +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", amount=" + amount +
                ", billStatus='" + billStatus + '\'' +
                ", payload=" + payload +
                '}';
    }
}