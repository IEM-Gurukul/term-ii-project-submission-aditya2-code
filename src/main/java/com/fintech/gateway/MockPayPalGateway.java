package com.fintech.gateway;

public class MockPayPalGateway extends AbstractMockGateway {
    @Override
    public TransactionResult process(Transaction t) throws GatewayTimeoutException {
        simulateTimeout();
        return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "PayPal: Payment successful");
    }
}
