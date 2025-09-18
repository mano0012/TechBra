package com.techbra.billing.application.usecases;

import com.techbra.billing.domain.model.Bill;
import com.techbra.billing.domain.model.BillStatus;
import com.techbra.billing.domain.events.BillPaidEvent;
import com.techbra.billing.domain.ports.out.BillRepositoryPort;
import com.techbra.billing.infrastructure.events.EventPublisher;
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
class BillUseCasesImplTest {

    @Mock
    private BillRepositoryPort billRepositoryPort;
    
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private BillUseCasesImpl billUseCases;

    private Bill testBill;
    private LocalDateTime testDueDate;

    @BeforeEach
    void setUp() {
        testDueDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        testBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        testBill.setId(1L);
    }

    @Test
    void getAllBills_ShouldReturnAllBills() {
        // Given
        List<Bill> expectedBills = Arrays.asList(testBill);
        when(billRepositoryPort.findAll()).thenReturn(expectedBills);

        // When
        List<Bill> actualBills = billUseCases.getAllBills();

        // Then
        assertEquals(expectedBills, actualBills);
        verify(billRepositoryPort).findAll();
    }

    @Test
    void getBillById_WhenBillExists_ShouldReturnBill() {
        // Given
        when(billRepositoryPort.findById(1L)).thenReturn(Optional.of(testBill));

        // When
        Optional<Bill> result = billUseCases.getBillById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBill, result.get());
        verify(billRepositoryPort).findById(1L);
    }

    @Test
    void getBillById_WhenBillDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(billRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Bill> result = billUseCases.getBillById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(billRepositoryPort).findById(1L);
    }

    @Test
    void createBill_ShouldSaveAndReturnBill() {
        // Given
        Bill newBill = new Bill("New Bill", new BigDecimal("200.00"), testDueDate);
        Bill savedBill = new Bill("New Bill", new BigDecimal("200.00"), testDueDate);
        savedBill.setId(2L);
        
        when(billRepositoryPort.save(any(Bill.class))).thenReturn(savedBill);

        // When
        Bill result = billUseCases.createBill(newBill);

        // Then
        assertEquals(savedBill, result);
        verify(billRepositoryPort).save(newBill);
    }

    @Test
    void updateBill_WhenBillExists_ShouldUpdateAndReturnBill() {
        // Given
        Bill updatedBill = new Bill("Updated Bill", new BigDecimal("150.00"), testDueDate);
        updatedBill.setId(1L);
        
        when(billRepositoryPort.existsById(1L)).thenReturn(true);
        when(billRepositoryPort.save(any(Bill.class))).thenReturn(updatedBill);

        // When
        Optional<Bill> result = billUseCases.updateBill(1L, updatedBill);

        // Then
        assertTrue(result.isPresent());
        assertEquals(updatedBill, result.get());
        assertEquals(1L, result.get().getId());
        verify(billRepositoryPort).existsById(1L);
        verify(billRepositoryPort).save(updatedBill);
    }

    @Test
    void updateBill_WhenBillDoesNotExist_ShouldReturnEmpty() {
        // Given
        Bill updatedBill = new Bill("Updated Bill", new BigDecimal("150.00"), testDueDate);
        
        when(billRepositoryPort.existsById(1L)).thenReturn(false);

        // When
        Optional<Bill> result = billUseCases.updateBill(1L, updatedBill);

        // Then
        assertFalse(result.isPresent());
        verify(billRepositoryPort).existsById(1L);
        verify(billRepositoryPort, never()).save(any(Bill.class));
    }

    @Test
    void deleteBill_WhenBillExists_ShouldDeleteAndReturnTrue() {
        // Given
        when(billRepositoryPort.existsById(1L)).thenReturn(true);

        // When
        boolean result = billUseCases.deleteBill(1L);

        // Then
        assertTrue(result);
        verify(billRepositoryPort).existsById(1L);
        verify(billRepositoryPort).deleteById(1L);
    }

    @Test
    void deleteBill_WhenBillDoesNotExist_ShouldReturnFalse() {
        // Given
        when(billRepositoryPort.existsById(1L)).thenReturn(false);

        // When
        boolean result = billUseCases.deleteBill(1L);

        // Then
        assertFalse(result);
        verify(billRepositoryPort).existsById(1L);
        verify(billRepositoryPort, never()).deleteById(anyLong());
    }
    
    @Test
    void payBill_WhenBillExistsAndNotPaid_ShouldMarkAsPaidAndPublishEvent() {
        // Given
        Bill unpaidBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        unpaidBill.setId(1L);
        
        Bill paidBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        paidBill.setId(1L);
        paidBill.markAsPaid();
        
        when(billRepositoryPort.findById(1L)).thenReturn(Optional.of(unpaidBill));
        when(billRepositoryPort.save(any(Bill.class))).thenReturn(paidBill);
        
        // When
        Optional<Bill> result = billUseCases.payBill(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(BillStatus.PAID, result.get().getStatus());
        assertNotNull(result.get().getPaidAt());
        
        verify(billRepositoryPort).findById(1L);
        verify(billRepositoryPort).save(any(Bill.class));
        verify(eventPublisher).publishBillPaidEvent(any(BillPaidEvent.class));
    }
    
    @Test
    void payBill_WhenBillExistsAndAlreadyPaid_ShouldReturnBillWithoutPublishingEvent() {
        // Given
        Bill alreadyPaidBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        alreadyPaidBill.setId(1L);
        alreadyPaidBill.markAsPaid();
        
        when(billRepositoryPort.findById(1L)).thenReturn(Optional.of(alreadyPaidBill));
        
        // When
        Optional<Bill> result = billUseCases.payBill(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(BillStatus.PAID, result.get().getStatus());
        
        verify(billRepositoryPort).findById(1L);
        verify(billRepositoryPort, never()).save(any(Bill.class));
        verify(eventPublisher, never()).publishBillPaidEvent(any(BillPaidEvent.class));
    }
    
    @Test
    void payBill_WhenBillDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(billRepositoryPort.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<Bill> result = billUseCases.payBill(1L);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(billRepositoryPort).findById(1L);
        verify(billRepositoryPort, never()).save(any(Bill.class));
        verify(eventPublisher, never()).publishBillPaidEvent(any(BillPaidEvent.class));
    }
}