package com.techbra.order.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Payload detalhado do evento de pedido
 * 
 * Esta classe contém informações detalhadas do pedido
 * que serão incluídas no evento publicado no Kafka.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class OrderPayload {

    @JsonProperty("orderNumber")
    private String orderNumber;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
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

    // Construtor padrão
    public OrderPayload() {}

    // Construtor com parâmetros
    public OrderPayload(String orderNumber, LocalDateTime createdAt, LocalDateTime updatedAt,
                       List<OrderItemPayload> items, AddressPayload shippingAddress,
                       AddressPayload billingAddress, String paymentMethod, String notes) {
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

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
        private UUID productId;

        @JsonProperty("productName")
        private String productName;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("unitPrice")
        private BigDecimal unitPrice;

        @JsonProperty("totalPrice")
        private BigDecimal totalPrice;

        // Construtor padrão
        public OrderItemPayload() {}

        // Construtor com parâmetros
        public OrderItemPayload(UUID productId, String productName, Integer quantity,
                               BigDecimal unitPrice, BigDecimal totalPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
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

        @JsonProperty("city")
        private String city;

        @JsonProperty("state")
        private String state;

        @JsonProperty("zipCode")
        private String zipCode;

        @JsonProperty("country")
        private String country;

        // Construtor padrão
        public AddressPayload() {}

        // Construtor com parâmetros
        public AddressPayload(String street, String city, String state, String zipCode, String country) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.country = country;
        }

        // Getters e Setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
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