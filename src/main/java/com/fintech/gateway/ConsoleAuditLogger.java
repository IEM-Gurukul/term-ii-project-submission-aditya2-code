package com.fintech.gateway;

import java.time.LocalDateTime;

/**
 * Implementation of AuditLogger that writes events to the console.
 */
public class ConsoleAuditLogger implements AuditLogger {
    @Override
    public void log(Transaction t, String event) {
        String timestamp = LocalDateTime.now().toString();
        String maskedToken = t.getRequest().getPaymentToken();
        if (maskedToken != null && maskedToken.length() > 4) {
            maskedToken = "****" + maskedToken.substring(maskedToken.length() - 4);
        } else {
            maskedToken = "****";
        }

        System.out.printf("[%s] [AUDIT] TransactionID: %s | Status: %s | Event: %s | Token: %s%n",
                timestamp, t.getTransactionId(), t.getStatus(), event, maskedToken);
    }
}
