package com.techbra.order.domain;

/**
 * Enum que representa os possíveis status de um pedido
 * 
 * @author TechBra Team
 * @version 1.0.0
 */
public enum OrderStatus {
    
    /**
     * Pedido criado mas ainda não confirmado
     */
    PENDING("Pendente", "Pedido criado e aguardando confirmação"),
    
    /**
     * Pedido confirmado e em processamento
     */
    CONFIRMED("Confirmado", "Pedido confirmado e sendo processado"),
    
    /**
     * Pedido em preparação
     */
    PREPARING("Preparando", "Pedido em preparação"),
    
    /**
     * Pedido pronto para envio
     */
    READY_FOR_SHIPPING("Pronto para Envio", "Pedido pronto para ser enviado"),
    
    /**
     * Pedido enviado
     */
    SHIPPED("Enviado", "Pedido foi enviado"),
    
    /**
     * Pedido entregue
     */
    DELIVERED("Entregue", "Pedido foi entregue com sucesso"),
    
    /**
     * Pedido cancelado
     */
    CANCELLED("Cancelado", "Pedido foi cancelado"),
    
    /**
     * Pedido com problema/erro
     */
    FAILED("Falhou", "Pedido falhou durante o processamento");
    
    private final String displayName;
    private final String description;
    
    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica se o status permite cancelamento
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED || this == PREPARING;
    }
    
    /**
     * Verifica se o status permite modificação
     */
    public boolean canBeModified() {
        return this == PENDING;
    }
    
    /**
     * Verifica se o pedido está em um estado final
     */
    public boolean isFinalStatus() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
    
    /**
     * Verifica se o pedido está ativo (não cancelado nem falhado)
     */
    public boolean isActive() {
        return this != CANCELLED && this != FAILED;
    }
}