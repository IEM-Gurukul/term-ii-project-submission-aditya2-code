package com.fintech.gateway;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Main entry point for the Fintech Payment Gateway Pipeline demo.
 * Demonstrates both single and batch processing flows.
 */
public class Main {
    /**
     * Entry point for the payment gateway demonstration.
     * Sets up the validation pipeline, processors, and mock gateways.
     * 
     * @param args command line arguments
     * @throws Exception if any unexpected error occurs during demo execution
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== FinTech Payment Gateway Pipeline Demo ===");
        
        // 1. Setup Validation Chain
        ValidationChain chain = new ValidationChain();
        chain.addStep(new SecurityValidator());
        chain.addStep(new FraudCheckValidator());
        BalanceValidator balanceValidator = new BalanceValidator();
        // Seed initial balance for demo
        balanceValidator.setBalance("tok_valid_123", new BigDecimal("1000.00"));
        balanceValidator.setBalance("tok_valid_01", new BigDecimal("500.00"));
        balanceValidator.setBalance("tok_valid_04", new BigDecimal("200.00"));
        balanceValidator.setBalance("tok_valid_05", new BigDecimal("300.00"));
        chain.addStep(balanceValidator);

        // 2. Setup Gateway & Audit Logger
        MockStripeGateway defaultGateway = new MockStripeGateway();
        defaultGateway.setFailureRate(0.0); // Predictable success for demo
        
        AuditLogger consoleLogger = new ConsoleAuditLogger();

        // 3. Initialize Processors
        PaymentProcessor singleProcessor = new PaymentProcessor(chain, defaultGateway, consoleLogger);

        // --- Single Processing Demo ---
        System.out.println("\n--- 1. Single Transaction Demo ---");
        TransactionRequest req1 = new TransactionRequest(new BigDecimal("150.00"), "USD", "tok_valid_123", "STRIPE");
        TransactionResult res1 = singleProcessor.process(req1);
        System.out.printf("Result: %s - %s%n", res1.getStatus(), res1.getMessage());

        // --- Batch Processing Demo ---
        System.out.println("\n--- 2. Batch Processing Demo ---");
        BatchPaymentProcessor batchProcessor = new BatchPaymentProcessor(4, singleProcessor);
        
        List<TransactionRequest> batchRequests = Arrays.asList(
            new TransactionRequest(new BigDecimal("50.00"), "USD", "tok_valid_01", "STRIPE"),
            new TransactionRequest(new BigDecimal("20000.00"), "USD", "tok_valid_02", "STRIPE"), // Should fail fraud
            new TransactionRequest(new BigDecimal("100.00"), "USD", null, "STRIPE"), // Should fail security null
            new TransactionRequest(new BigDecimal("75.00"), "EUR", "tok_valid_04", "PAYPAL"),
            new TransactionRequest(new BigDecimal("120.00"), "USD", "tok_valid_05", "STRIPE")
        );
        
        List<Future<TransactionResult>> futures = batchProcessor.processBatch(batchRequests);
        
        // Wait for all to complete
        for (Future<TransactionResult> f : futures) {
            TransactionResult r = f.get(); // Blocking wait
            System.out.printf("Batch Result: %s [%s] - %s%n", r.getTransactionId(), r.getStatus(), r.getMessage());
        }
        
        batchProcessor.shutdown();
        System.out.println("\n=== Demo Completed ===");
    }
}
