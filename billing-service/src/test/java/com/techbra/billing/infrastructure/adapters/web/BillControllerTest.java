package com.techbra.billing.infrastructure.adapters.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbra.billing.domain.model.Bill;
import com.techbra.billing.domain.model.BillStatus;
import com.techbra.billing.domain.ports.in.BillUseCases;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillController.class)
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillUseCases billUseCases;

    @Autowired
    private ObjectMapper objectMapper;

    private Bill testBill;
    private LocalDateTime testDueDate;

    @BeforeEach
    void setUp() {
        testDueDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        testBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        testBill.setId(1L);
    }

    @Test
    void getAllBills_ShouldReturnListOfBills() throws Exception {
        // Given
        List<Bill> bills = Arrays.asList(testBill);
        when(billUseCases.getAllBills()).thenReturn(bills);

        // When & Then
        mockMvc.perform(get("/api/bills"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test Bill"))
                .andExpect(jsonPath("$[0].amount").value(100.50))
                .andExpect(jsonPath("$[0].dueDate").value("2024-12-31T23:59:59"));

        verify(billUseCases).getAllBills();
    }

    @Test
    void getAllBills_WhenNoBills_ShouldReturnEmptyList() throws Exception {
        // Given
        when(billUseCases.getAllBills()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/bills"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(billUseCases).getAllBills();
    }

    @Test
    void getBillById_WhenBillExists_ShouldReturnBill() throws Exception {
        // Given
        when(billUseCases.getBillById(1L)).thenReturn(Optional.of(testBill));

        // When & Then
        mockMvc.perform(get("/api/bills/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test Bill"))
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.dueDate").value("2024-12-31T23:59:59"));

        verify(billUseCases).getBillById(1L);
    }

    @Test
    void getBillById_WhenBillDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(billUseCases.getBillById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/bills/1"))
                .andExpect(status().isNotFound());

        verify(billUseCases).getBillById(1L);
    }

    @Test
    void createBill_WithValidBill_ShouldReturnCreatedBill() throws Exception {
        // Given
        Bill newBill = new Bill("New Bill", new BigDecimal("200.00"), testDueDate);
        Bill createdBill = new Bill("New Bill", new BigDecimal("200.00"), testDueDate);
        createdBill.setId(2L);
        
        when(billUseCases.createBill(any(Bill.class))).thenReturn(createdBill);

        // When & Then
        mockMvc.perform(post("/api/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBill)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.description").value("New Bill"))
                .andExpect(jsonPath("$.amount").value(200.00));

        verify(billUseCases).createBill(any(Bill.class));
    }

    @Test
    void updateBill_WhenBillExists_ShouldReturnUpdatedBill() throws Exception {
        // Given
        Bill updatedBill = new Bill("Updated Bill", new BigDecimal("150.00"), testDueDate);
        updatedBill.setId(1L);
        
        when(billUseCases.updateBill(eq(1L), any(Bill.class))).thenReturn(Optional.of(updatedBill));

        // When & Then
        mockMvc.perform(put("/api/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBill)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Updated Bill"))
                .andExpect(jsonPath("$.amount").value(150.00));

        verify(billUseCases).updateBill(eq(1L), any(Bill.class));
    }

    @Test
    void updateBill_WhenBillDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        Bill updatedBill = new Bill("Updated Bill", new BigDecimal("150.00"), testDueDate);
        
        when(billUseCases.updateBill(eq(1L), any(Bill.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBill)))
                .andExpect(status().isNotFound());

        verify(billUseCases).updateBill(eq(1L), any(Bill.class));
    }

    @Test
    void deleteBill_WhenBillExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(billUseCases.deleteBill(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/bills/1"))
                .andExpect(status().isNoContent());

        verify(billUseCases).deleteBill(1L);
    }

    @Test
    void deleteBill_WhenBillDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(billUseCases.deleteBill(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/bills/1"))
                .andExpect(status().isNotFound());

        verify(billUseCases).deleteBill(1L);
    }

    @Test
    void createBill_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(billUseCases, never()).createBill(any(Bill.class));
    }

    @Test
    void getBillById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bills/invalid"))
                .andExpect(status().isBadRequest());

        verify(billUseCases, never()).getBillById(anyLong());
    }
    
    @Test
    void payBill_WhenBillExists_ShouldReturnPaidBill() throws Exception {
        // Given
        Bill paidBill = new Bill("Test Bill", new BigDecimal("100.50"), testDueDate);
        paidBill.setId(1L);
        paidBill.markAsPaid();
        
        when(billUseCases.payBill(1L)).thenReturn(Optional.of(paidBill));
        
        // When & Then
        mockMvc.perform(put("/api/bills/1/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paidAt").exists());
        
        verify(billUseCases).payBill(1L);
    }
    
    @Test
    void payBill_WhenBillDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(billUseCases.payBill(1L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(put("/api/bills/1/pay"))
                .andExpect(status().isNotFound());
        
        verify(billUseCases).payBill(1L);
    }
}