package com.techbra.billing.infrastructure.adapters.persistence;

import com.techbra.billing.domain.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillJpaRepository extends JpaRepository<Bill, Long> {
}