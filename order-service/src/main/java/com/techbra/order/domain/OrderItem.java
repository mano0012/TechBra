package com.techbra.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa um Item de Pedido
 * 
 * Esta classe representa um produto específico dentro de um pedido,
 * incluindo quantidade, preço e informações do produto.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class OrderItem {
    
    private UUID id;
    private Order order;
    private UUID productId;
    private String productName;
    private String productSku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String productDescription;
    private String productImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
    
    // Construtor padrão
    public OrderItem() {
        this.quantity = 0;
        this.unitPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
        this.version = 0L;
    }
    
    // Construtor para criação de novo item
    public OrderItem(UUID productId, String productName, String productSku, 
                     BigDecimal unitPrice, Integer quantity) {
        this();
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }
    
    // Construtor completo
    public OrderItem(UUID productId, String productName, String productSku, 
                     BigDecimal unitPrice, Integer quantity, String productDescription, 
                     String productImageUrl) {
        this(productId, productName, productSku, unitPrice, quantity);
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
    }
    
    // Métodos de negócio
    
    /**
     * Atualiza a quantidade do item
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        if (this.order != null && !this.order.getStatus().canBeModified()) {
            throw new IllegalStateException("Não é possível alterar a quantidade de itens em um pedido com status: " + 
                                          this.order.getStatus().getDisplayName());
        }
        
        this.quantity = newQuantity;
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Atualiza o preço unitário do item
     */
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice == null || newUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço unitário deve ser positivo");
        }
        
        if (this.order != null && !this.order.getStatus().canBeModified()) {
            throw new IllegalStateException("Não é possível alterar o preço de itens em um pedido com status: " + 
                                          this.order.getStatus().getDisplayName());
        }
        
        this.unitPrice = newUnitPrice;
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calcula o preço total do item (quantidade * preço unitário)
     */
    private void calculateTotalPrice() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
    
    /**
     * Verifica se o item é válido
     */
    public boolean isValid() {
        return this.productId != null && 
               this.productName != null && !this.productName.trim().isEmpty() &&
               this.unitPrice != null && this.unitPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               this.quantity != null && this.quantity > 0;
    }
    
    /**
     * Cria uma cópia do item para outro pedido
     */
    public OrderItem copy() {
        OrderItem copy = new OrderItem();
        copy.setProductId(this.productId);
        copy.setProductName(this.productName);
        copy.setProductSku(this.productSku);
        copy.setUnitPrice(this.unitPrice);
        copy.setQuantity(this.quantity);
        copy.setProductDescription(this.productDescription);
        copy.setProductImageUrl(this.productImageUrl);
        copy.calculateTotalPrice();
        return copy;
    }
    
    // Getters e Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductSku() {
        return productSku;
    }
    
    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public String getProductImageUrl() {
        return productImageUrl;
    }
    
    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productSku='" + productSku + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }
}