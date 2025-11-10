package com.payan.demo.controller;

import com.payan.demo.entity.Transaction;
import com.payan.demo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAuthenticatedUserAccessesDashboard_thenReturnDashboardView() throws Exception {
        // Given
        List<Transaction> mockTransactions = Arrays.asList(
                createMockTransaction(1L, "TXN001", "Salary Deposit", new BigDecimal("5000.00"), "CREDIT"),
                createMockTransaction(2L, "TXN002", "Grocery Shopping", new BigDecimal("150.00"), "DEBIT")
        );
        when(transactionService.getAllTransactions()).thenReturn(mockTransactions);

        // When & Then
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("username"));
    }

    @Test
    void whenUnauthenticatedUserAccessesDashboard_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void whenRegularUserAccessesDashboard_thenSuccess() throws Exception {
        // Given
        List<Transaction> mockTransactions = Arrays.asList(
                createMockTransaction(1L, "TXN001", "Payment", new BigDecimal("100.00"), "DEBIT")
        );
        when(transactionService.getAllTransactions()).thenReturn(mockTransactions);

        // When & Then
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    private Transaction createMockTransaction(Long id, String transactionId, String description, 
                                              BigDecimal amount, String type) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setTransactionId(transactionId);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory("Test Category");
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setReference("REF" + id);
        return transaction;
    }
}
