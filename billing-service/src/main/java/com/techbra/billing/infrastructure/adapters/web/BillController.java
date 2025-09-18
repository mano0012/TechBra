package com.techbra.billing.infrastructure.adapters.web;

import com.techbra.billing.domain.model.Bill;
import com.techbra.billing.domain.ports.in.BillUseCases;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
public class BillController {
    
    private final BillUseCases billUseCases;
    
    public BillController(BillUseCases billUseCases) {
        this.billUseCases = billUseCases;
    }
    
    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billUseCases.getAllBills();
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        Optional<Bill> bill = billUseCases.getBillById(id);
        return bill.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Bill> createBill(@RequestBody Bill bill) {
        Bill createdBill = billUseCases.createBill(bill);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
    }
    
    @PutMapping("/{id}/pay")
    public ResponseEntity<Bill> payBill(@PathVariable Long id) {
        Optional<Bill> paidBill = billUseCases.payBill(id);
        return paidBill.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Bill> updateBill(@PathVariable Long id, @RequestBody Bill bill) {
        Optional<Bill> updatedBill = billUseCases.updateBill(id, bill);
        return updatedBill.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        boolean deleted = billUseCases.deleteBill(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
}