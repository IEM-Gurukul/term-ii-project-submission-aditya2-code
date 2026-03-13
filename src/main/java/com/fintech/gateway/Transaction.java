package com.fintech.gateway;

public class Transaction {
    private final String transactionId;
    private final TransactionRequest request;
    private TransactionStatus status;

    public Transaction(String transactionId, TransactionRequest request) {
        this.transactionId = transactionId;
        this.request = request;
        this.status = TransactionStatus.PENDING;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionRequest getRequest() {
        return request;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", request=" + request +
                ", status=" + status +
                '}';
    }
}
