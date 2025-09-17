package com.techbra.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa um Pedido
 * 
 * Esta classe segue os princípios de Domain-Driven Design (DDD)
 * e contém a lógica de negócio relacionada aos pedidos.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class Order {
    
    private UUID id;
    private String orderNumber;
    private UUID customerId;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private String notes;
    private String customerNotes;
    private String internalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private Long version;
    private List<OrderItem> items;
    
    // Construtor padrão
    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.shippingAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
        this.version = 0L;
    }
    
    // Construtor para criação de novo pedido
    public Order(UUID customerId, String notes) {
        this();
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.notes = notes;
        this.customerNotes = notes;
        this.orderNumber = generateOrderNumber();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Métodos de negócio
    
    /**
     * Adiciona um item ao pedido
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }
        
        if (!status.canBeModified()) {
            throw new IllegalStateException("Não é possível adicionar itens a um pedido com status: " + status.getDisplayName());
        }
        
        item.setOrder(this);
        this.items.add(item);
        recalculateAmounts();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Remove um item do pedido
     */
    public void removeItem(OrderItem item) {
        if (!status.canBeModified()) {
            throw new IllegalStateException("Não é possível remover itens de um pedido com status: " + status.getDisplayName());
        }
        
        this.items.remove(item);
        recalculateAmounts();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Confirma o pedido
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Apenas pedidos pendentes podem ser confirmados");
        }
        
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar um pedido sem itens");
        }
        
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cancela o pedido
     */
    public void cancel(String reason) {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException("Não é possível cancelar um pedido com status: " + status.getDisplayName());
        }
        
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (reason != null && !reason.trim().isEmpty()) {
            this.internalNotes = (this.internalNotes != null ? this.internalNotes + "\n" : "") + 
                               "Cancelado: " + reason;
        }
    }
    
    /**
     * Atualiza o status do pedido
     */
    public void updateStatus(OrderStatus newStatus) {
        if (this.status.isFinalStatus()) {
            throw new IllegalStateException("Não é possível alterar o status de um pedido finalizado");
        }
        
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // Atualiza timestamps específicos baseado no status
        switch (newStatus) {
            case SHIPPED:
                this.shippedAt = LocalDateTime.now();
                break;
            case DELIVERED:
                this.deliveredAt = LocalDateTime.now();
                break;
            case CANCELLED:
                this.cancelledAt = LocalDateTime.now();
                break;
        }
    }
    
    /**
     * Aplica desconto ao pedido
     */
    public void applyDiscount(BigDecimal discountAmount) {
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do desconto deve ser positivo");
        }
        
        if (discountAmount.compareTo(this.totalAmount) > 0) {
            throw new IllegalArgumentException("Desconto não pode ser maior que o valor total");
        }
        
        this.discountAmount = discountAmount;
        this.finalAmount = this.totalAmount.subtract(this.discountAmount);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Recalcula os valores do pedido baseado nos itens
     */
    public void recalculateAmounts() {
        this.totalAmount = this.items.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.finalAmount = this.totalAmount.subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
    }
    
    /**
     * Gera um número único para o pedido
     */
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
    
    /**
     * Retorna a quantidade total de itens no pedido
     */
    public int getTotalItemsCount() {
        return this.items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }
    
    /**
     * Verifica se o pedido pode ser cancelado
     */
    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }
    
    /**
     * Verifica se o pedido pode ser modificado
     */
    public boolean canBeModified() {
        return this.status == OrderStatus.PENDING;
    }
    
    // Getters e Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public String getCustomerNotes() {
        return customerNotes;
    }
    
    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }
    
    public String getInternalNotes() {
        return internalNotes;
    }
    
    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }
    
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public List<OrderItem> getItems() {
        return new ArrayList<>(items); // Retorna cópia para proteger encapsulamento
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        recalculateAmounts();
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }
    
    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerId=" + customerId +
                ", status=" + status +
                ", finalAmount=" + finalAmount +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}