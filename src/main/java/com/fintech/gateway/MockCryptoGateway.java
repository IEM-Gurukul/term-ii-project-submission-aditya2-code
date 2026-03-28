package com.fintech.gateway;

/**
 * A mock implementation of a Cryptocurrency payment gateway for simulation purposes.
 */
public class MockCryptoGateway extends AbstractMockGateway {
    @Override
    public TransactionResult process(Transaction t) throws GatewayTimeoutException {
        simulateTimeout();
        return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "Crypto: Payment successful");
    }
}
