package com.techbra.logistics.infrastructure.web.dto;

import com.techbra.logistics.domain.model.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequestDto {
    
    @NotNull(message = "Status é obrigatório")
    private ShipmentStatus status;
    
    private String reason;
    
    // Construtores
    public UpdateStatusRequestDto() {}
    
    public UpdateStatusRequestDto(ShipmentStatus status) {
        this.status = status;
    }
    
    public UpdateStatusRequestDto(ShipmentStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    // Getters e Setters
    public ShipmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String toString() {
        return "UpdateStatusRequestDto{" +
                "status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}