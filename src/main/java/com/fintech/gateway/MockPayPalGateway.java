package com.fintech.gateway;

/**
 * A mock implementation of PayPal payment gateway for simulation purposes.
 */
public class MockPayPalGateway extends AbstractMockGateway {
    @Override
    public TransactionResult process(Transaction t) throws GatewayTimeoutException {
        simulateTimeout();
        return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "PayPal: Payment successful");
    }
}
