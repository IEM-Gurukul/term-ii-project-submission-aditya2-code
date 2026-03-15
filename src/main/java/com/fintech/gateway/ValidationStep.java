package com.fintech.gateway;

/**
 * Defines a single validation step in the payment pipeline.
 */
public interface ValidationStep {
    /**
     * Validates a transaction.
     *
     * @param t the transaction to validate
     * @throws ValidationException if the validation fails
     */
    void validate(Transaction t) throws ValidationException;
}
