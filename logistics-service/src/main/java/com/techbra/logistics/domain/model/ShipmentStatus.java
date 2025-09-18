package com.techbra.logistics.domain.model;

public enum ShipmentStatus {
    PENDING("Pendente", "Aguardando processamento"),
    PROCESSING("Processando", "Preparando para envio"),
    SHIPPED("Enviado", "Produto enviado para entrega"),
    IN_TRANSIT("Em trânsito", "Produto em transporte"),
    OUT_FOR_DELIVERY("Saiu para entrega", "Produto saiu para entrega final"),
    DELIVERED("Entregue", "Produto entregue com sucesso"),
    FAILED_DELIVERY("Falha na entrega", "Tentativa de entrega falhou"),
    RETURNED("Devolvido", "Produto retornado ao remetente"),
    CANCELLED("Cancelado", "Envio cancelado");

    private final String displayName;
    private final String description;

    ShipmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(ShipmentStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == IN_TRANSIT || newStatus == FAILED_DELIVERY;
            case IN_TRANSIT -> newStatus == OUT_FOR_DELIVERY || newStatus == FAILED_DELIVERY;
            case OUT_FOR_DELIVERY -> newStatus == DELIVERED || newStatus == FAILED_DELIVERY;
            case FAILED_DELIVERY -> newStatus == OUT_FOR_DELIVERY || newStatus == RETURNED;
            case DELIVERED, RETURNED, CANCELLED -> false; // Estados finais
        };
    }

    public boolean isFinalStatus() {
        return this == DELIVERED || this == RETURNED || this == CANCELLED;
    }
}