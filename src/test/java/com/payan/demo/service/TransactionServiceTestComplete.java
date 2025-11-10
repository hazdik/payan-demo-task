package com.payan.demo.service;

import com.payan.demo.entity.Transaction;
import com.payan.demo.repository.TransactionRepository;
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
public class TransactionServiceTestComplete {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

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
    void testCreateTransaction_WithTransactionId() {
        // Arrange
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        assertEquals("TXN-12345678", result.getTransactionId());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_WithoutTransactionId() {
        // Arrange
        testTransaction.setTransactionId(null);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_WithoutDate() {
        // Arrange
        testTransaction.setTransactionDate(null);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetAllTransactions_Success() {
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
        when(transactionRepository.findAllByOrderByTransactionDateDesc()).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findAllByOrderByTransactionDateDesc();
    }

    @Test
    void testGetTransactionById_Found() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act
        Optional<Transaction> result = transactionService.getTransactionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TXN-12345678", result.get().getTransactionId());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionById_NotFound() {
        // Arrange
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Transaction> result = transactionService.getTransactionById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById(999L);
    }

    @Test
    void testGetTransactionsByStatus_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByStatus("COMPLETED")).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByStatus("COMPLETED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("COMPLETED", result.get(0).getStatus());
        verify(transactionRepository, times(1)).findByStatus("COMPLETED");
    }

    @Test
    void testGetTransactionsByType_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByType("CREDIT")).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByType("CREDIT");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREDIT", result.get(0).getType());
        verify(transactionRepository, times(1)).findByType("CREDIT");
    }

    @Test
    void testGetTransactionsByCategory_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByCategory("Salary")).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByCategory("Salary");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).getCategory());
        verify(transactionRepository, times(1)).findByCategory("Salary");
    }

    @Test
    void testUpdateTransaction_Success() {
        // Arrange
        Transaction updatedDetails = new Transaction();
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setAmount(new BigDecimal("150.00"));
        updatedDetails.setType("DEBIT");
        updatedDetails.setStatus("PENDING");
        updatedDetails.setCategory("Shopping");
        updatedDetails.setReference("REF-002");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.updateTransaction(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testUpdateTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Transaction updatedDetails = new Transaction();
        updatedDetails.setDescription("Updated Description");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(999L, updatedDetails);
        });
        verify(transactionRepository, times(1)).findById(999L);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_Success() {
        // Arrange
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.saveTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    void testDeleteTransaction_Success() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(1L);

        // Act
        transactionService.deleteTransaction(1L);

        // Assert
        verify(transactionRepository, times(1)).existsById(1L);
        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_NotFound() {
        // Arrange
        when(transactionRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.deleteTransaction(999L);
        });
        verify(transactionRepository, times(1)).existsById(999L);
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateTransactionStatus_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.updateTransactionStatus(1L, "FAILED");

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testUpdateTransactionStatus_NotFound() {
        // Arrange
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransactionStatus(999L, "FAILED");
        });
        verify(transactionRepository, times(1)).findById(999L);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_EmptyTransactionId() {
        // Arrange
        testTransaction.setTransactionId("");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
