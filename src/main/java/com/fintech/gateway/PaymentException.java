package com.fintech.gateway;

/**
 * Root exception for all payment-related issues.
 */
public abstract class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
