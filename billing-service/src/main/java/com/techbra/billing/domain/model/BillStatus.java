package com.techbra.billing.domain.model;

/**
 * Enum que representa os possíveis status de uma fatura.
 */
public enum BillStatus {
    /**
     * Status inicial quando a fatura é criada.
     */
    CREATED,
    
    /**
     * Status quando a fatura foi paga.
     */
    PAID
}