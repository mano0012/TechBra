package com.techbra.order.web.dto;

import com.techbra.order.domain.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para representar um pedido
 * 
 * Esta classe é utilizada para serializar dados de pedidos
 * nas respostas das APIs REST.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class OrderResponse {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("orderNumber")
    private String orderNumber;
    
    @JsonProperty("customerId")
    private UUID customerId;
    
    @JsonProperty("status")
    private OrderStatus status;
    
    @JsonProperty("statusDescription")
    private String statusDescription;
    
    @JsonProperty("subtotal")
    private BigDecimal subtotal;
    
    @JsonProperty("taxAmount")
    private BigDecimal taxAmount;
    
    @JsonProperty("shippingAmount")
    private BigDecimal shippingAmount;
    
    @JsonProperty("discountAmount")
    private BigDecimal discountAmount;
    
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    
    @JsonProperty("shippingAddress")
    private String shippingAddress;
    
    @JsonProperty("billingAddress")
    private String billingAddress;
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("items")
    private List<OrderItemResponse> items;
    
    @JsonProperty("itemCount")
    private Integer itemCount;
    
    @JsonProperty("totalQuantity")
    private Integer totalQuantity;
    
    @JsonProperty("canBeModified")
    private Boolean canBeModified;
    
    @JsonProperty("canBeCancelled")
    private Boolean canBeCancelled;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonProperty("isFinal")
    private Boolean isFinal;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonProperty("version")
    private Long version;
    
    // Construtor padrão
    public OrderResponse() {}
    
    // Construtor com parâmetros principais
    public OrderResponse(UUID id, String orderNumber, UUID customerId, OrderStatus status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.status = status;
        this.statusDescription = status != null ? status.getDisplayName() : null;
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
        this.statusDescription = status != null ? status.getDisplayName() : null;
    }
    
    public String getStatusDescription() {
        return statusDescription;
    }
    
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
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
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
    
    public List<OrderItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
        this.itemCount = items != null ? items.size() : 0;
        this.totalQuantity = items != null ? 
            items.stream().mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0).sum() : 0;
    }
    
    public Integer getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public Boolean getCanBeModified() {
        return canBeModified;
    }
    
    public void setCanBeModified(Boolean canBeModified) {
        this.canBeModified = canBeModified;
    }
    
    public Boolean getCanBeCancelled() {
        return canBeCancelled;
    }
    
    public void setCanBeCancelled(Boolean canBeCancelled) {
        this.canBeCancelled = canBeCancelled;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsFinal() {
        return isFinal;
    }
    
    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
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
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    /**
     * Converte um objeto de domínio Order para OrderResponse
     * 
     * @param order objeto de domínio
     * @return OrderResponse correspondente
     */
    public static OrderResponse fromDomain(com.techbra.order.domain.Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setStatusDescription(order.getStatus().getDescription());
        response.setSubtotal(order.getSubtotal());
        response.setTaxAmount(order.getTaxAmount());
        response.setShippingAmount(order.getShippingAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setVersion(order.getVersion());
        
        // Converter itens se existirem
        if (order.getItems() != null) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(OrderItemResponse::fromDomain)
                    .collect(java.util.stream.Collectors.toList());
            response.setItems(itemResponses);
            response.setItemCount(itemResponses.size());
        }
        
        return response;
    }
    
    @Override
    public String toString() {
        return "OrderResponse{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerId=" + customerId +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + itemCount +
                '}';
    }
}