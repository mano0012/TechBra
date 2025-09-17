package com.techbra.order.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO de request para aplicar desconto ao pedido
 * 
 * Esta classe é utilizada para receber dados de aplicação de desconto
 * em pedidos existentes nas requisições das APIs REST.
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class ApplyDiscountRequest {
    
    @JsonProperty("discountAmount")
    @NotNull(message = "Valor do desconto é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor do desconto deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Valor do desconto deve ter no máximo 10 dígitos inteiros e 2 decimais")
    private BigDecimal discountAmount;
    
    @JsonProperty("discountReason")
    @NotBlank(message = "Motivo do desconto é obrigatório")
    @Size(max = 255, message = "Motivo do desconto deve ter no máximo 255 caracteres")
    private String discountReason;
    
    @JsonProperty("couponCode")
    @Size(max = 50, message = "Código do cupom deve ter no máximo 50 caracteres")
    private String couponCode;
    
    @JsonProperty("discountType")
    @Size(max = 20, message = "Tipo do desconto deve ter no máximo 20 caracteres")
    private String discountType;
    
    // Construtor padrão
    public ApplyDiscountRequest() {}
    
    // Construtor com parâmetros principais
    public ApplyDiscountRequest(BigDecimal discountAmount, String discountReason) {
        this.discountAmount = discountAmount;
        this.discountReason = discountReason;
    }
    
    // Construtor completo
    public ApplyDiscountRequest(BigDecimal discountAmount, String discountReason, 
                              String couponCode, String discountType) {
        this.discountAmount = discountAmount;
        this.discountReason = discountReason;
        this.couponCode = couponCode;
        this.discountType = discountType;
    }
    
    // Getters e Setters
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public String getDiscountReason() {
        return discountReason;
    }
    
    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }
    
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public String getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
    
    /**
     * Verifica se o desconto tem código de cupom
     * 
     * @return true se há código de cupom
     */
    public boolean hasCouponCode() {
        return couponCode != null && !couponCode.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "ApplyDiscountRequest{" +
                "discountAmount=" + discountAmount +
                ", discountReason='" + discountReason + '\'' +
                ", couponCode='" + couponCode + '\'' +
                ", discountType='" + discountType + '\'' +
                '}';
    }
}