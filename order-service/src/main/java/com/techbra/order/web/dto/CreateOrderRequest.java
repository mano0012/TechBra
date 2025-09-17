package com.techbra.order.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * DTO de request para criação de pedidos
 * 
 * Esta classe é utilizada para receber dados de criação de pedidos
 * nas requisições das APIs REST.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class CreateOrderRequest {
    
    @JsonProperty("customerId")
    @NotNull(message = "ID do cliente é obrigatório")
    private UUID customerId;
    
    @JsonProperty("shippingAddress")
    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(max = 500, message = "Endereço de entrega deve ter no máximo 500 caracteres")
    private String shippingAddress;
    
    @JsonProperty("billingAddress")
    @NotBlank(message = "Endereço de cobrança é obrigatório")
    @Size(max = 500, message = "Endereço de cobrança deve ter no máximo 500 caracteres")
    private String billingAddress;
    
    @JsonProperty("paymentMethod")
    @NotBlank(message = "Método de pagamento é obrigatório")
    @Size(max = 100, message = "Método de pagamento deve ter no máximo 100 caracteres")
    private String paymentMethod;
    
    @JsonProperty("notes")
    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String notes;
    
    @JsonProperty("items")
    @Valid
    private List<OrderItemRequest> items;
    
    // Construtor padrão
    public CreateOrderRequest() {}
    
    // Construtor com parâmetros principais
    public CreateOrderRequest(UUID customerId, String shippingAddress, 
                            String billingAddress, String paymentMethod) {
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters e Setters
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
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
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    /**
     * Verifica se o pedido deve ser criado com itens
     * 
     * @return true se há itens na lista
     */
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }
    
    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "customerId=" + customerId +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}