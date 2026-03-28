package com.fintech.gateway;

/**
 * A mock implementation of Stripe payment gateway for simulation purposes.
 */
public class MockStripeGateway extends AbstractMockGateway {
    @Override
    public TransactionResult process(Transaction t) throws GatewayTimeoutException {
        simulateTimeout();
        return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "Stripe: Payment successful");
    }
}
