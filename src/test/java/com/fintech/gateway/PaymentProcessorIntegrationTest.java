package com.fintech.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessorIntegrationTest {
    private PaymentProcessor processor;
    private ValidationChain chain;
    private MockStripeGateway stripeGateway;
    private List<String> logs;
    private AuditLogger mockAuditLogger;

    @BeforeEach
    void setUp() {
        chain = new ValidationChain();
        stripeGateway = new MockStripeGateway();
        stripeGateway.setFailureRate(0.0);
        logs = new ArrayList<>();
        
        mockAuditLogger = (t, event) -> logs.add(event);
        
        processor = new PaymentProcessor(chain, stripeGateway, mockAuditLogger);
    }

    @Test
    void testSuccessfulFlow() {
        chain.addStep(new SecurityValidator());
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", "valid_token_long", "STRIPE");
        
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(logs.contains("PENDING"));
        assertTrue(logs.contains("APPROVED"));
    }

    @Test
    void testValidationFailureFlow() {
        chain.addStep(new SecurityValidator());
        // Short token triggers SecurityValidator failure
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", "short", "STRIPE");
        
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertTrue(logs.contains("PENDING"));
        assertTrue(logs.stream().anyMatch(s -> s.contains("DECLINED")));
    }

    @Test
    void testGatewayTimeoutFlow() {
        stripeGateway.setFailureRate(1.0); // Force timeout
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "USD", "valid_token_long", "STRIPE");
        
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertTrue(logs.contains("FAILED: Gateway timeout"));
    }

    @Test
    void testRuntimeGatewaySwitching() throws GatewayTimeoutException {
        MockPayPalGateway paypalGateway = new MockPayPalGateway();
        paypalGateway.setFailureRate(0.0);
        
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), "USD", "valid_token_long", "PAYPAL");
        
        // Switch to PayPal at runtime
        processor.setGateway(paypalGateway);
        TransactionResult result = processor.process(request);
        
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("PayPal"));
    }
}
