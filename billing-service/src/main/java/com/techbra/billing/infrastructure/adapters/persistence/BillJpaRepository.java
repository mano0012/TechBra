package com.techbra.billing.infrastructure.adapters.persistence;

import com.techbra.billing.domain.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillJpaRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByOrderId(UUID orderId);
}