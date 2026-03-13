package com.fintech.gateway;

import java.time.LocalDateTime;

public class TransactionResult {
    private final String transactionId;
    private final TransactionStatus status;
    private final String message;
    private final LocalDateTime timestamp;

    public TransactionResult(String transactionId, TransactionStatus status, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

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
