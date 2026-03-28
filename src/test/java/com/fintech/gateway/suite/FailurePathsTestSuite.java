package com.fintech.gateway.suite;

import com.fintech.gateway.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite covering all failure and edge-case paths for the Payment Gateway Pipeline.
 */
public class FailurePathsTestSuite {

    private PaymentProcessor processor;
    private ValidationChain chain;
    private MockStripeGateway stripeGateway;
    private BalanceValidator balanceValidator;
    private List<String> logs;
    private AuditLogger mockAuditLogger;

    @BeforeEach
    void setUp() {
        chain = new ValidationChain();
        chain.addStep(new SecurityValidator());
        chain.addStep(new FraudCheckValidator());
        balanceValidator = new BalanceValidator();
        chain.addStep(balanceValidator);

        stripeGateway = new MockStripeGateway();
        stripeGateway.setFailureRate(0.0);

        logs = new ArrayList<>();
        mockAuditLogger = (t, event) -> logs.add(event);
        
        processor = new PaymentProcessor(chain, stripeGateway, mockAuditLogger);
    }

    @Test
    void testValidationFailure_SecurityNullToken() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", null, "STRIPE");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertTrue(result.getMessage().contains("Payment token is missing"));
    }

    @Test
    void testValidationFailure_SecurityShortToken() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", "short", "STRIPE");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertTrue(result.getMessage().contains("Invalid payment token format"));
    }

    @Test
    void testValidationFailure_FraudHighAmount() {
        // Set balance so it passes BalanceValidator, ensuring FraudCheckValidator triggers
        balanceValidator.setBalance("valid_token_123", new BigDecimal("50000.00"));
        TransactionRequest request = new TransactionRequest(new BigDecimal("15000.00"), "USD", "valid_token_123", "STRIPE");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertTrue(result.getMessage().contains("exceeds maximum allowed limit"));
    }

    @Test
    void testValidationFailure_BalanceInsufficient() {
        // Set some balance but not enough
        balanceValidator.setBalance("valid_token_123", new BigDecimal("1000.00"));
        TransactionRequest request = new TransactionRequest(new BigDecimal("2000.00"), "USD", "valid_token_123", "STRIPE");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertTrue(result.getMessage().contains("Insufficient funds"));
    }

    @Test
    void testGatewayTimeout_ExhaustsRetries() {
        // Set balance so validation passes, then gateway timeout triggers
        balanceValidator.setBalance("valid_token_123", new BigDecimal("10000.00"));
        stripeGateway.setFailureRate(1.0); // 100% failure rate
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", "valid_token_123", "STRIPE");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertTrue(result.getMessage().contains("Gateway timeout"));
    }
}
