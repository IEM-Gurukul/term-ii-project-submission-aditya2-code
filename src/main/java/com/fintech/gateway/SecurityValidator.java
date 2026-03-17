package com.fintech.gateway;

/**
 * Implements security validation for transactions.
 * Checks for null tokens and ensures token format is valid.
 */
public class SecurityValidator implements ValidationStep {

    @Override
    public void validate(Transaction t) throws ValidationException {
        if (t == null || t.getRequest() == null) {
            throw new ValidationException("Invalid transaction request");
        }

        String token = t.getRequest().getPaymentToken();
        if (token == null || token.trim().isEmpty()) {
            throw new ValidationException("Payment token is missing");
        }

        // Simple format check: assume tokens must be at least 8 characters
        if (token.length() < 8) {
            throw new ValidationException("Invalid payment token format");
        }
    }
}
