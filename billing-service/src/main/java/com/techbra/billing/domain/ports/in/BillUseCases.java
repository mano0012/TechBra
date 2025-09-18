package com.techbra.billing.domain.ports.in;

import com.techbra.billing.domain.model.Bill;
import java.util.List;
import java.util.Optional;

public interface BillUseCases {
    List<Bill> getAllBills();
    Optional<Bill> getBillById(Long id);
    Bill createBill(Bill bill);
    Optional<Bill> updateBill(Long id, Bill bill);
    boolean deleteBill(Long id);
    Optional<Bill> payBill(Long id);
}