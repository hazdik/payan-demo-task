package com.payan.demo.config;

import com.payan.demo.entity.Transaction;
import com.payan.demo.entity.User;
import com.payan.demo.repository.TransactionRepository;
import com.payan.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Load sample users
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setEmail("admin@payan.com");
            admin.setRole("ADMIN");
            admin.setEnabled(true);

            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setFullName("John Doe");
            user1.setEmail("john.doe@example.com");
            user1.setRole("USER");
            user1.setEnabled(true);

            User user2 = new User();
            user2.setUsername("user2");
            user2.setPassword(passwordEncoder.encode("password123"));
            user2.setFullName("Jane Smith");
            user2.setEmail("jane.smith@example.com");
            user2.setRole("USER");
            user2.setEnabled(true);

            userRepository.saveAll(Arrays.asList(admin, user1, user2));
            System.out.println("✅ Sample users loaded successfully!");
        }

        // Load sample transactions
        if (transactionRepository.count() == 0) {
            List<Transaction> transactions = Arrays.asList(
                createTransaction("TXN001", "Salary Deposit", new BigDecimal("5000.00"), 
                    "CREDIT", "COMPLETED", "Salary", "SAL-2024-01"),
                
                createTransaction("TXN002", "Grocery Shopping", new BigDecimal("150.50"), 
                    "DEBIT", "COMPLETED", "Food & Dining", "GRC-2024-01"),
                
                createTransaction("TXN003", "Electric Bill Payment", new BigDecimal("80.00"), 
                    "DEBIT", "COMPLETED", "Utilities", "BILL-2024-01"),
                
                createTransaction("TXN004", "Freelance Project Payment", new BigDecimal("1200.00"), 
                    "CREDIT", "COMPLETED", "Income", "FRL-2024-01"),
                
                createTransaction("TXN005", "Online Shopping - Electronics", new BigDecimal("450.00"), 
                    "DEBIT", "COMPLETED", "Shopping", "AMZ-2024-01"),
                
                createTransaction("TXN006", "Restaurant Dinner", new BigDecimal("85.75"), 
                    "DEBIT", "COMPLETED", "Food & Dining", "REST-2024-01"),
                
                createTransaction("TXN007", "Bank Interest Credit", new BigDecimal("25.50"), 
                    "CREDIT", "COMPLETED", "Interest", "INT-2024-01"),
                
                createTransaction("TXN008", "Gym Membership", new BigDecimal("60.00"), 
                    "DEBIT", "PENDING", "Health & Fitness", null),
                
                createTransaction("TXN009", "Insurance Premium", new BigDecimal("200.00"), 
                    "DEBIT", "COMPLETED", "Insurance", "INS-2024-01"),
                
                createTransaction("TXN010", "Stock Dividend", new BigDecimal("150.00"), 
                    "CREDIT", "COMPLETED", "Investment", "DIV-2024-01"),
                
                createTransaction("TXN011", "Car Fuel", new BigDecimal("65.00"), 
                    "DEBIT", "COMPLETED", "Transportation", "FUEL-2024-01"),
                
                createTransaction("TXN012", "Mobile Recharge", new BigDecimal("30.00"), 
                    "DEBIT", "FAILED", "Utilities", null),
                
                createTransaction("TXN013", "Rent Payment", new BigDecimal("1500.00"), 
                    "DEBIT", "COMPLETED", "Housing", "RENT-2024-01"),
                
                createTransaction("TXN014", "Refund - Online Order", new BigDecimal("75.00"), 
                    "CREDIT", "PENDING", "Refund", null),
                
                createTransaction("TXN015", "Coffee Shop", new BigDecimal("12.50"), 
                    "DEBIT", "COMPLETED", "Food & Dining", "CAFE-2024-01")
            );

            transactionRepository.saveAll(transactions);
            System.out.println("✅ Sample transactions loaded successfully!");
            System.out.println("📊 Total transactions loaded: " + transactions.size());
        }
    }

    private Transaction createTransaction(String txnId, String description, BigDecimal amount,
                                         String type, String status, String category, String reference) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(txnId);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setCategory(category);
        transaction.setReference(reference);
        
        // Set transaction date (random dates within last 30 days)
        int daysAgo = (int) (Math.random() * 30);
        transaction.setTransactionDate(LocalDateTime.now().minusDays(daysAgo).minusHours((int)(Math.random() * 24)));
        
        return transaction;
    }
}
