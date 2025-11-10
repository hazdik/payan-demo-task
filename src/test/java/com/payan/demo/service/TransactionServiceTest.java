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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setTransactionId("TXN001");
        transaction1.setDescription("Salary Deposit");
        transaction1.setAmount(new BigDecimal("5000.00"));
        transaction1.setType("CREDIT");
        transaction1.setCategory("Salary");
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(LocalDateTime.now());
        transaction1.setReference("REF001");

        transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setTransactionId("TXN002");
        transaction2.setDescription("Grocery Shopping");
        transaction2.setAmount(new BigDecimal("150.00"));
        transaction2.setType("DEBIT");
        transaction2.setCategory("Food & Dining");
        transaction2.setStatus("COMPLETED");
        transaction2.setTransactionDate(LocalDateTime.now());
        transaction2.setReference("REF002");
    }

    @Test
    void getAllTransactions_shouldReturnAllTransactions() {
        // Given
        List<Transaction> expectedTransactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAllByOrderByTransactionDateDesc()).thenReturn(expectedTransactions);

        // When
        List<Transaction> actualTransactions = transactionService.getAllTransactions();

        // Then
        assertNotNull(actualTransactions);
        assertEquals(2, actualTransactions.size());
        assertEquals("TXN001", actualTransactions.get(0).getTransactionId());
        assertEquals("TXN002", actualTransactions.get(1).getTransactionId());
        verify(transactionRepository, times(1)).findAllByOrderByTransactionDateDesc();
    }

    @Test
    void getAllTransactions_whenNoTransactions_shouldReturnEmptyList() {
        // Given
        when(transactionRepository.findAllByOrderByTransactionDateDesc()).thenReturn(Arrays.asList());

        // When
        List<Transaction> actualTransactions = transactionService.getAllTransactions();

        // Then
        assertNotNull(actualTransactions);
        assertTrue(actualTransactions.isEmpty());
        verify(transactionRepository, times(1)).findAllByOrderByTransactionDateDesc();
    }

    @Test
    void getAllTransactions_shouldReturnTransactionsOrderedByDateDescending() {
        // Given
        List<Transaction> expectedTransactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAllByOrderByTransactionDateDesc()).thenReturn(expectedTransactions);

        // When
        List<Transaction> actualTransactions = transactionService.getAllTransactions();

        // Then
        assertNotNull(actualTransactions);
        assertEquals(2, actualTransactions.size());
        // Verify repository method was called
        verify(transactionRepository, times(1)).findAllByOrderByTransactionDateDesc();
    }
}
