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
 * Test suite covering all success paths for the Payment Gateway Pipeline.
 */
public class SuccessPathsTestSuite {

    private PaymentProcessor processor;
    private ValidationChain chain;
    private MockStripeGateway stripeGateway;
    private MockPayPalGateway paypalGateway;
    private MockCryptoGateway cryptoGateway;
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

        // Pre-load balances for all test tokens
        balanceValidator.setBalance("valid_token_12345", new BigDecimal("10000.00"));
        balanceValidator.setBalance("valid_token_67890", new BigDecimal("10000.00"));
        balanceValidator.setBalance("valid_token_crypto", new BigDecimal("10000.00"));

        stripeGateway = new MockStripeGateway();
        stripeGateway.setFailureRate(0.0);
        
        paypalGateway = new MockPayPalGateway();
        paypalGateway.setFailureRate(0.0);

        cryptoGateway = new MockCryptoGateway();
        cryptoGateway.setFailureRate(0.0);

        logs = new ArrayList<>();
        mockAuditLogger = (t, event) -> logs.add(event);
        
        processor = new PaymentProcessor(chain, stripeGateway, mockAuditLogger);
    }

    @Test
    void testSuccessfulStripeTransaction() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", "valid_token_12345", "STRIPE");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("Stripe"));
    }

    @Test
    void testSuccessfulPayPalTransaction() {
        processor.setGateway(paypalGateway);
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), "EUR", "valid_token_67890", "PAYPAL");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("PayPal"));
    }

    @Test
    void testSuccessfulCryptoTransaction() {
        processor.setGateway(cryptoGateway);
        TransactionRequest request = new TransactionRequest(new BigDecimal("500.00"), "BTC", "valid_token_crypto", "CRYPTO");
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("Crypto"));
    }
}
