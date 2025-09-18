package com.techbra.billing.infrastructure.adapters.persistence;

import com.techbra.billing.domain.model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillRepositoryAdapterTest {

    @Mock
    private BillJpaRepository billJpaRepository;

    @InjectMocks
    private BillRepositoryAdapter billRepositoryAdapter;

    private Bill testBill;
    private LocalDateTime testDueDate;

    @BeforeEach
    void setUp() {
        testDueDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        testBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        testBill.setId(1L);
    }

    @Test
    void findAll_ShouldReturnAllBills() {
        // Given
        List<Bill> expectedBills = Arrays.asList(testBill);
        when(billJpaRepository.findAll()).thenReturn(expectedBills);

        // When
        List<Bill> actualBills = billRepositoryAdapter.findAll();

        // Then
        assertEquals(expectedBills, actualBills);
        verify(billJpaRepository).findAll();
    }

    @Test
    void findById_WhenBillExists_ShouldReturnBill() {
        // Given
        when(billJpaRepository.findById(1L)).thenReturn(Optional.of(testBill));

        // When
        Optional<Bill> result = billRepositoryAdapter.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBill, result.get());
        verify(billJpaRepository).findById(1L);
    }

    @Test
    void findById_WhenBillDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(billJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Bill> result = billRepositoryAdapter.findById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(billJpaRepository).findById(1L);
    }

    @Test
    void save_ShouldSaveAndReturnBill() {
        // Given
        Bill billToSave = new Bill("New Bill", new BigDecimal("200.00"), testDueDate);
        Bill savedBill = new Bill("New Bill", new BigDecimal("200.00"), testDueDate);
        savedBill.setId(2L);
        
        when(billJpaRepository.save(any(Bill.class))).thenReturn(savedBill);

        // When
        Bill result = billRepositoryAdapter.save(billToSave);

        // Then
        assertEquals(savedBill, result);
        verify(billJpaRepository).save(billToSave);
    }

    @Test
    void deleteById_ShouldCallJpaRepositoryDelete() {
        // Given
        Long billId = 1L;

        // When
        billRepositoryAdapter.deleteById(billId);

        // Then
        verify(billJpaRepository).deleteById(billId);
    }

    @Test
    void existsById_WhenBillExists_ShouldReturnTrue() {
        // Given
        when(billJpaRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = billRepositoryAdapter.existsById(1L);

        // Then
        assertTrue(result);
        verify(billJpaRepository).existsById(1L);
    }

    @Test
    void existsById_WhenBillDoesNotExist_ShouldReturnFalse() {
        // Given
        when(billJpaRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = billRepositoryAdapter.existsById(1L);

        // Then
        assertFalse(result);
        verify(billJpaRepository).existsById(1L);
    }

    @Test
    void save_WithNullBill_ShouldThrowException() {
        // Given
        when(billJpaRepository.save(null)).thenThrow(new IllegalArgumentException("Bill cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            billRepositoryAdapter.save(null);
        });
        
        verify(billJpaRepository).save(null);
    }

    @Test
    void findById_WithNullId_ShouldThrowException() {
        // Given
        when(billJpaRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            billRepositoryAdapter.findById(null);
        });
        
        verify(billJpaRepository).findById(null);
    }
}