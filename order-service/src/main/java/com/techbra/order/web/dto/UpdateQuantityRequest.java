package com.techbra.order.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de atualização de quantidade de item
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public class UpdateQuantityRequest {
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantity;
    
    public UpdateQuantityRequest() {}
    
    public UpdateQuantityRequest(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}