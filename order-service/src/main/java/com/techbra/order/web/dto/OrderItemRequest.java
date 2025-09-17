package com.techbra.order.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de request para itens de pedido
 * 
 * Esta classe é utilizada para receber dados de itens de pedidos
 * nas requisições das APIs REST.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class OrderItemRequest {
    
    @JsonProperty("productId")
    @NotNull(message = "ID do produto é obrigatório")
    private UUID productId;
    
    @JsonProperty("productName")
    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(max = 255, message = "Nome do produto deve ter no máximo 255 caracteres")
    private String productName;
    
    @JsonProperty("productSku")
    @Size(max = 100, message = "SKU do produto deve ter no máximo 100 caracteres")
    private String productSku;
    
    @JsonProperty("productDescription")
    @Size(max = 1000, message = "Descrição do produto deve ter no máximo 1000 caracteres")
    private String productDescription;
    
    @JsonProperty("productImageUrl")
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    private String productImageUrl;
    
    @JsonProperty("unitPrice")
    @NotNull(message = "Preço unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Preço unitário deve ter no máximo 10 dígitos inteiros e 2 decimais")
    private BigDecimal unitPrice;
    
    @JsonProperty("quantity")
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @Max(value = 9999, message = "Quantidade deve ser menor que 10000")
    private Integer quantity;
    
    // Construtor padrão
    public OrderItemRequest() {}
    
    // Construtor com parâmetros principais
    public OrderItemRequest(UUID productId, String productName, 
                          BigDecimal unitPrice, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    
    // Getters e Setters
    
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
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    /**
     * Calcula o preço total do item
     * 
     * @return preço total (unitPrice * quantity)
     */
    public BigDecimal getTotalPrice() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "OrderItemRequest{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productSku='" + productSku + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}