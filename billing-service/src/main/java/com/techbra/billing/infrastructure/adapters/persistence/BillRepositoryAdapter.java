package com.techbra.billing.infrastructure.adapters.persistence;

import com.techbra.billing.domain.model.Bill;
import com.techbra.billing.domain.ports.out.BillRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BillRepositoryAdapter implements BillRepositoryPort {
    
    private final BillJpaRepository billJpaRepository;
    
    public BillRepositoryAdapter(BillJpaRepository billJpaRepository) {
        this.billJpaRepository = billJpaRepository;
    }
    
    @Override
    public List<Bill> findAll() {
        return billJpaRepository.findAll();
    }
    
    @Override
    public Optional<Bill> findById(Long id) {
        return billJpaRepository.findById(id);
    }
    
    @Override
    public Optional<Bill> findByOrderId(UUID orderId) {
        return billJpaRepository.findByOrderId(orderId);
    }
    
    @Override
    public Bill save(Bill bill) {
        return billJpaRepository.save(bill);
    }
    
    @Override
    public void deleteById(Long id) {
        billJpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return billJpaRepository.existsById(id);
    }
}