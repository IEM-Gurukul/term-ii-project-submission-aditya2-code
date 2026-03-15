package com.fintech.gateway;

/**
 * Exception thrown when a validation step fails.
 * This is typically a non-recoverable error.
 */
public class ValidationException extends PaymentException {
    public ValidationException(String message) {
        super(message);
    }
}
