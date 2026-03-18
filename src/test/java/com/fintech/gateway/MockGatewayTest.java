package com.fintech.gateway;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MockGatewayTest {
    private Transaction createTransaction() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("10.00"), "USD", "token123", "MOCK");
        return new Transaction("TXN_MOCK", request);
    }

    @Test
    void testStripeGatewaySuccess() throws GatewayTimeoutException {
        MockStripeGateway gateway = new MockStripeGateway();
        gateway.setFailureRate(0.0);
        TransactionResult result = gateway.process(createTransaction());
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("Stripe"));
    }

    @Test
    void testPayPalGatewaySuccess() throws GatewayTimeoutException {
        MockPayPalGateway gateway = new MockPayPalGateway();
        gateway.setFailureRate(0.0);
        TransactionResult result = gateway.process(createTransaction());
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("PayPal"));
    }

    @Test
    void testCryptoGatewaySuccess() throws GatewayTimeoutException {
        MockCryptoGateway gateway = new MockCryptoGateway();
        gateway.setFailureRate(0.0);
        TransactionResult result = gateway.process(createTransaction());
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertTrue(result.getMessage().contains("Crypto"));
    }

    @Test
    void testGatewayTimeoutSimulation() {
        MockStripeGateway gateway = new MockStripeGateway();
        gateway.setFailureRate(1.0); // Always fail
        assertThrows(GatewayTimeoutException.class, () -> gateway.process(createTransaction()));
    }
}
