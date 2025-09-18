package com.techbra.billing.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Payload com dados detalhados do pedido recebido via evento Kafka
 */
public class OrderPayload {
    
    @JsonProperty("orderNumber")
    private String orderNumber;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonProperty("items")
    private List<OrderItemPayload> items;
    
    @JsonProperty("shippingAddress")
    private AddressPayload shippingAddress;
    
    @JsonProperty("billingAddress")
    private AddressPayload billingAddress;
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    
    @JsonProperty("notes")
    private String notes;

    // Construtores
    public OrderPayload() {}

    // Getters e Setters
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public List<OrderItemPayload> getItems() {
        return items;
    }

    public void setItems(List<OrderItemPayload> items) {
        this.items = items;
    }

    public AddressPayload getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressPayload shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public AddressPayload getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressPayload billingAddress) {
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

    /**
     * Classe interna para representar itens do pedido
     */
    public static class OrderItemPayload {
        @JsonProperty("productId")
        private Long productId;
        
        @JsonProperty("productName")
        private String productName;
        
        @JsonProperty("quantity")
        private Integer quantity;
        
        @JsonProperty("unitPrice")
        private BigDecimal unitPrice;
        
        @JsonProperty("totalPrice")
        private BigDecimal totalPrice;

        // Construtores
        public OrderItemPayload() {}

        // Getters e Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }

    /**
     * Classe interna para representar endereços
     */
    public static class AddressPayload {
        @JsonProperty("street")
        private String street;
        
        @JsonProperty("number")
        private String number;
        
        @JsonProperty("complement")
        private String complement;
        
        @JsonProperty("neighborhood")
        private String neighborhood;
        
        @JsonProperty("city")
        private String city;
        
        @JsonProperty("state")
        private String state;
        
        @JsonProperty("zipCode")
        private String zipCode;
        
        @JsonProperty("country")
        private String country;

        // Construtores
        public AddressPayload() {}

        // Getters e Setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getComplement() {
            return complement;
        }

        public void setComplement(String complement) {
            this.complement = complement;
        }

        public String getNeighborhood() {
            return neighborhood;
        }

        public void setNeighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}