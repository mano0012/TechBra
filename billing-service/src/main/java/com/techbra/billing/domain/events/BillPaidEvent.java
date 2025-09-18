package com.techbra.billing.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de domínio disparado quando uma fatura é paga.
 */
public class BillPaidEvent {
    private final Long billId;
    private final String description;
    private final BigDecimal amount;
    private final LocalDateTime paidAt;
    private final LocalDateTime occurredAt;

    public BillPaidEvent(Long billId, String description, BigDecimal amount, LocalDateTime paidAt) {
        this.billId = billId;
        this.description = description;
        this.amount = amount;
        this.paidAt = paidAt;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getBillId() {
        return billId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "BillPaidEvent{" +
                "billId=" + billId +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", paidAt=" + paidAt +
                ", occurredAt=" + occurredAt +
                '}';
    }
}