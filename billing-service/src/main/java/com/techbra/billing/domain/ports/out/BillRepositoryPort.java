package com.techbra.billing.domain.ports.out;

import com.techbra.billing.domain.model.Bill;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepositoryPort {
    List<Bill> findAll();
    Optional<Bill> findById(Long id);
    Optional<Bill> findByOrderId(UUID orderId);
    Bill save(Bill bill);
    void deleteById(Long id);
    boolean existsById(Long id);
}