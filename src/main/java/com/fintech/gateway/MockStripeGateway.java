package com.fintech.gateway;

public class MockStripeGateway extends AbstractMockGateway {
    @Override
    public TransactionResult process(Transaction t) throws GatewayTimeoutException {
        simulateTimeout();
        return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "Stripe: Payment successful");
    }
}
