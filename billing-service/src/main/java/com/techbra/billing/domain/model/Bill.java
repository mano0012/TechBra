package com.techbra.billing.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    public Bill() {
        this.createdAt = LocalDateTime.now();
        this.status = BillStatus.CREATED;
    }
    
    public Bill(String description, BigDecimal amount, LocalDateTime dueDate) {
        this();
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
    
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    
    /**
     * Marca a fatura como paga.
     */
    public void markAsPaid() {
        this.status = BillStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
    
    /**
     * Verifica se a fatura foi paga.
     */
    public boolean isPaid() {
        return this.status == BillStatus.PAID;
    }
}