package com.payan.demo.repository;

import com.payan.demo.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        transaction1 = new Transaction();
        transaction1.setTransactionId("TXN001");
        transaction1.setDescription("Salary Deposit");
        transaction1.setAmount(new BigDecimal("5000.00"));
        transaction1.setType("CREDIT");
        transaction1.setCategory("Salary");
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(LocalDateTime.now().minusDays(2));
        transaction1.setReference("REF001");

        transaction2 = new Transaction();
        transaction2.setTransactionId("TXN002");
        transaction2.setDescription("Grocery Shopping");
        transaction2.setAmount(new BigDecimal("150.00"));
        transaction2.setType("DEBIT");
        transaction2.setCategory("Food & Dining");
        transaction2.setStatus("COMPLETED");
        transaction2.setTransactionDate(LocalDateTime.now().minusDays(1));
        transaction2.setReference("REF002");

        transaction3 = new Transaction();
        transaction3.setTransactionId("TXN003");
        transaction3.setDescription("Online Purchase");
        transaction3.setAmount(new BigDecimal("299.99"));
        transaction3.setType("DEBIT");
        transaction3.setCategory("Shopping");
        transaction3.setStatus("PENDING");
        transaction3.setTransactionDate(LocalDateTime.now());
        transaction3.setReference("REF003");
    }

    @Test
    void findAllByOrderByTransactionDateDesc_shouldReturnTransactionsInDescendingOrder() {
        // Given
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.persist(transaction3);
        entityManager.flush();

        // When
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDesc();

        // Then
        assertNotNull(transactions);
        assertEquals(3, transactions.size());
        
        // Verify descending order (most recent first)
        assertEquals("TXN003", transactions.get(0).getTransactionId());
        assertEquals("TXN002", transactions.get(1).getTransactionId());
        assertEquals("TXN001", transactions.get(2).getTransactionId());
    }

    @Test
    void findAllByOrderByTransactionDateDesc_whenNoTransactions_shouldReturnEmptyList() {
        // When
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDesc();

        // Then
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void saveTransaction_shouldPersistTransaction() {
        // When
        Transaction savedTransaction = transactionRepository.save(transaction1);

        // Then
        assertNotNull(savedTransaction.getId());
        assertEquals("TXN001", savedTransaction.getTransactionId());
        assertEquals("Salary Deposit", savedTransaction.getDescription());
        assertEquals(new BigDecimal("5000.00"), savedTransaction.getAmount());
        assertEquals("CREDIT", savedTransaction.getType());
    }

    @Test
    void findAll_shouldReturnAllTransactions() {
        // Given
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        // When
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDesc();

        // Then
        assertEquals(2, transactions.size());
    }

    @Test
    void saveTransaction_withAllFields_shouldPersistCorrectly() {
        // When
        Transaction savedTransaction = transactionRepository.save(transaction3);
        entityManager.flush();
        Transaction foundTransaction = entityManager.find(Transaction.class, savedTransaction.getId());

        // Then
        assertNotNull(foundTransaction);
        assertEquals("TXN003", foundTransaction.getTransactionId());
        assertEquals("Online Purchase", foundTransaction.getDescription());
        assertEquals(new BigDecimal("299.99"), foundTransaction.getAmount());
        assertEquals("DEBIT", foundTransaction.getType());
        assertEquals("Shopping", foundTransaction.getCategory());
        assertEquals("PENDING", foundTransaction.getStatus());
        assertEquals("REF003", foundTransaction.getReference());
        assertNotNull(foundTransaction.getTransactionDate());
    }

    @Test
    void findAllByOrderByTransactionDateDesc_shouldHandleSameDateTime() {
        // Given
        LocalDateTime sameTime = LocalDateTime.now();
        transaction1.setTransactionDate(sameTime);
        transaction2.setTransactionDate(sameTime);
        
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        // When
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDesc();

        // Then
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
    }
}
