package com.techbra.billing.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Payload adicional do evento de logística
 */
public class LogisticsPayload {
    
    @JsonProperty("billDescription")
    private String billDescription;
    
    @JsonProperty("dueDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("paidAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paidAt;
    
    @JsonProperty("processingNotes")
    private String processingNotes;

    // Construtores
    public LogisticsPayload() {}

    public LogisticsPayload(String billDescription, LocalDateTime dueDate, 
                           LocalDateTime createdAt, LocalDateTime paidAt, 
                           String processingNotes) {
        this.billDescription = billDescription;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.processingNotes = processingNotes;
    }

    // Getters and Setters
    public String getBillDescription() { return billDescription; }
    public void setBillDescription(String billDescription) { this.billDescription = billDescription; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getProcessingNotes() { return processingNotes; }
    public void setProcessingNotes(String processingNotes) { this.processingNotes = processingNotes; }

    @Override
    public String toString() {
        return "LogisticsPayload{" +
                "billDescription='" + billDescription + '\'' +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", processingNotes='" + processingNotes + '\'' +
                '}';
    }
}