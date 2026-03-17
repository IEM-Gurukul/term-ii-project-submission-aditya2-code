package com.fintech.gateway;

import java.math.BigDecimal;

/**
 * Implements fraud check validation for transactions.
 * Checks for suspicious amounts or velocity rules.
 */
public class FraudCheckValidator implements ValidationStep {
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("10000.00");

    @Override
    public void validate(Transaction t) throws ValidationException {
        BigDecimal amount = t.getRequest().getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transaction amount must be positive");
        }

        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            throw new ValidationException("Transaction amount exceeds maximum allowed limit (Fraud Alert)");
        }
    }
}
