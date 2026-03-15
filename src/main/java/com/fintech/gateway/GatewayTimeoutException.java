package com.fintech.gateway;

/**
 * Exception thrown when a gateway request times out.
 * This is typically a recoverable error that may be retried.
 */
public class GatewayTimeoutException extends PaymentException {
    public GatewayTimeoutException(String message) {
        super(message);
    }

    public GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
