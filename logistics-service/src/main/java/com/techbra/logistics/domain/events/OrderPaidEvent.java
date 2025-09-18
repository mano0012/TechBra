package com.techbra.logistics.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderPaidEvent {
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String deliveryAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private BigDecimal totalAmount;
    private LocalDateTime paidAt;
    private LocalDateTime occurredAt;

    public OrderPaidEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    public OrderPaidEvent(Long orderId, String customerName, String customerEmail,
                         String deliveryAddress, String city, String state,
                         String zipCode, String country, BigDecimal totalAmount,
                         LocalDateTime paidAt) {
        this();
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.totalAmount = totalAmount;
        this.paidAt = paidAt;
    }

    // Getters
    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    // Setters (para deserialização)
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPaidEvent that = (OrderPaidEvent) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, occurredAt);
    }

    @Override
    public String toString() {
        return "OrderPaidEvent{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", city='" + city + '\'' +
                ", totalAmount=" + totalAmount +
                ", paidAt=" + paidAt +
                ", occurredAt=" + occurredAt +
                '}';
    }
}