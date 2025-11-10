package com.payan.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payan.demo.entity.Transaction;
import com.payan.demo.service.TransactionService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setTransactionId("TXN-12345678");
        testTransaction.setDescription("Test Transaction");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setType("CREDIT");
        testTransaction.setStatus("COMPLETED");
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setCategory("Salary");
        testTransaction.setReference("REF-001");
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        // Arrange
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(testTransaction);

        // Act & Assert
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId", is("TXN-12345678")))
                .andExpect(jsonPath("$.description", is("Test Transaction")))
                .andExpect(jsonPath("$.type", is("CREDIT")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        // Arrange
        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setTransactionId("TXN-87654321");
        transaction2.setDescription("Test Transaction 2");
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setType("DEBIT");
        transaction2.setStatus("PENDING");
        transaction2.setTransactionDate(LocalDateTime.now());
        transaction2.setCategory("Shopping");

        List<Transaction> transactions = Arrays.asList(testTransaction, transaction2);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].transactionId", is("TXN-12345678")))
                .andExpect(jsonPath("$[1].transactionId", is("TXN-87654321")));
    }

    @Test
    void testGetAllTransactions_NoContent() throws Exception {
        // Arrange
        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTransactionById_Found() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(testTransaction));

        // Act & Assert
        mockMvc.perform(get("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId", is("TXN-12345678")))
                .andExpect(jsonPath("$.description", is("Test Transaction")));
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTransactionsByStatus_Success() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByStatus("COMPLETED")).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/status/COMPLETED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("COMPLETED")));
    }

    @Test
    void testGetTransactionsByStatus_NoContent() throws Exception {
        // Arrange
        when(transactionService.getTransactionsByStatus(anyString())).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/transactions/status/PENDING")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTransactionsByType_Success() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByType("CREDIT")).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/type/CREDIT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("CREDIT")));
    }

    @Test
    void testGetTransactionsByType_NoContent() throws Exception {
        // Arrange
        when(transactionService.getTransactionsByType(anyString())).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/transactions/type/DEBIT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTransactionsByCategory_Success() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByCategory("Salary")).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/category/Salary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Salary")));
    }

    @Test
    void testGetTransactionsByCategory_NoContent() throws Exception {
        // Arrange
        when(transactionService.getTransactionsByCategory(anyString())).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/transactions/category/Shopping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        // Arrange
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setDescription("Updated Description");
        updatedTransaction.setAmount(new BigDecimal("150.00"));
        updatedTransaction.setType("DEBIT");
        updatedTransaction.setStatus("PENDING");
        updatedTransaction.setCategory("Shopping");
        updatedTransaction.setReference("REF-002");

        when(transactionService.updateTransaction(eq(1L), any(Transaction.class)))
                .thenReturn(updatedTransaction);

        // Act & Assert
        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.type", is("DEBIT")));
    }

    @Test
    void testUpdateTransaction_NotFound() throws Exception {
        // Arrange
        when(transactionService.updateTransaction(anyLong(), any(Transaction.class)))
                .thenThrow(new RuntimeException("Transaction not found"));

        // Act & Assert
        mockMvc.perform(put("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransaction)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTransactionStatus_Success() throws Exception {
        // Arrange
        testTransaction.setStatus("FAILED");
        when(transactionService.updateTransactionStatus(1L, "FAILED")).thenReturn(testTransaction);

        // Act & Assert
        mockMvc.perform(patch("/api/transactions/1/status")
                .param("status", "FAILED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("FAILED")));
    }

    @Test
    void testUpdateTransactionStatus_NotFound() throws Exception {
        // Arrange
        when(transactionService.updateTransactionStatus(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Transaction not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/transactions/999/status")
                .param("status", "FAILED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTransaction_Success() throws Exception {
        // Arrange
        doNothing().when(transactionService).deleteTransaction(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTransaction_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Transaction not found"))
                .when(transactionService).deleteTransaction(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
