package com.techbra.billing.domain.ports.out;

import com.techbra.billing.domain.model.Bill;
import java.util.List;
import java.util.Optional;

public interface BillRepositoryPort {
    List<Bill> findAll();
    Optional<Bill> findById(Long id);
    Bill save(Bill bill);
    void deleteById(Long id);
    boolean existsById(Long id);
}