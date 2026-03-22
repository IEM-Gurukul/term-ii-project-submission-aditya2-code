package com.fintech.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles transaction retries with exponential back-off.
 */
public class RetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);
    
    private final int maxRetries;
    private final long baseDelayMs;

    public RetryHandler(int maxRetries, long baseDelayMs) {
        this.maxRetries = maxRetries;
        this.baseDelayMs = baseDelayMs;
    }

    /**
     * Executes a task with retries.
     *
     * @param task the task to execute
     * @param transaction the transaction context
     * @param auditLogger the logger for auditing attempts
     * @return the result of the task
     * @throws PaymentException if all retries fail or a non-recoverable error occurs
     */
    public TransactionResult executeWithRetry(RetryableTask task, Transaction transaction, AuditLogger auditLogger) throws PaymentException {
        int attempt = 0;
        while (true) {
            try {
                return task.execute();
            } catch (GatewayTimeoutException e) {
                attempt++;
                if (attempt > maxRetries) {
                    logger.error("Max retries ({}) exhausted for transaction {}", maxRetries, transaction.getTransactionId());
                    throw e;
                }

                long delay = calculateBackoff(attempt);
                logger.warn("Retry attempt {}/{} for transaction {} after {}ms delay", 
                        attempt, maxRetries, transaction.getTransactionId(), delay);
                
                if (auditLogger != null) {
                    auditLogger.log(transaction, String.format("RETRY_ATTEMPT_%d_DELAY_%dms", attempt, delay));
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PaymentException("Retry interrupted", ie);
                }
            }
        }
    }

    private long calculateBackoff(int attempt) {
        // wait = baseDelay * 2^(attempt-1)
        return baseDelayMs * (long) Math.pow(2, attempt - 1);
    }

    @FunctionalInterface
    public interface RetryableTask {
        TransactionResult execute() throws GatewayTimeoutException;
    }
}
