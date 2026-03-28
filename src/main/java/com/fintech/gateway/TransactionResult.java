package com.fintech.gateway;

import java.time.LocalDateTime;

/**
 * Represents the final outcome of a processed transaction.
 */
public class TransactionResult {
    private final String transactionId;
    private final TransactionStatus status;
    private final String message;
    private final LocalDateTime timestamp;

    /**
     * Constructs a TransactionResult.
     * @param transactionId the unique transaction identifier
     * @param status the final determined status
     * @param message an accompanying system message or reason
     */
    public TransactionResult(String transactionId, TransactionStatus status, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Gets the transaction ID.
     * @return the transaction identifier
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Gets the transaction status outcome.
     * @return the transaction status
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Gets the descriptive message associated with the outcome.
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the timestamp denoting when the result was generated.
     * @return the result timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TransactionResult{" +
                "transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
