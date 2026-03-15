package com.fintech.gateway;

/**
 * Defines the contract for external payment gateway integrations.
 */
public interface PaymentGateway {
    /**
     * Processes a transaction through the gateway.
     *
     * @param t the transaction to process
     * @return the result of the transaction
     * @throws GatewayTimeoutException if the gateway request times out
     */
    TransactionResult process(Transaction t) throws GatewayTimeoutException;
}
