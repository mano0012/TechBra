package com.techbra.billing.application.usecases;

import com.techbra.billing.domain.model.Bill;
import com.techbra.billing.domain.ports.in.BillUseCases;
import com.techbra.billing.domain.ports.out.BillRepositoryPort;
import com.techbra.billing.domain.events.BillPaidEvent;
import com.techbra.billing.infrastructure.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BillUseCasesImpl implements BillUseCases {
    
    private final BillRepositoryPort billRepositoryPort;
    private final EventPublisher eventPublisher;
    
    public BillUseCasesImpl(BillRepositoryPort billRepositoryPort, EventPublisher eventPublisher) {
        this.billRepositoryPort = billRepositoryPort;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public List<Bill> getAllBills() {
        return billRepositoryPort.findAll();
    }
    
    @Override
    public Optional<Bill> getBillById(Long id) {
        return billRepositoryPort.findById(id);
    }
    
    @Override
    public Bill createBill(Bill bill) {
        return billRepositoryPort.save(bill);
    }
    
    @Override
    public Optional<Bill> updateBill(Long id, Bill bill) {
        if (billRepositoryPort.existsById(id)) {
            bill.setId(id);
            return Optional.of(billRepositoryPort.save(bill));
        }
        return Optional.empty();
    }
    
    @Override
    public boolean deleteBill(Long id) {
        if (billRepositoryPort.existsById(id)) {
            billRepositoryPort.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public Optional<Bill> payBill(Long id) {
        Optional<Bill> billOpt = billRepositoryPort.findById(id);
        
        if (billOpt.isPresent()) {
            Bill bill = billOpt.get();
            
            if (!bill.isPaid()) {
                bill.markAsPaid();
                Bill paidBill = billRepositoryPort.save(bill);
                
                // Criar e publicar evento
                BillPaidEvent event = new BillPaidEvent(
                    paidBill.getId(),
                    paidBill.getDescription(),
                    paidBill.getAmount(),
                    paidBill.getPaidAt()
                );
                
                eventPublisher.publishBillPaidEvent(event);
                
                return Optional.of(paidBill);
            }
            return Optional.of(bill); // Já está paga
        }
        
        return Optional.empty();
    }
}