package com.fintech.gateway;

/**
 * Defines the contract for logging transaction events.
 */
public interface AuditLogger {
    /**
     * Logs an event related to a transaction.
     *
     * @param t the transaction being logged
     * @param event a description of the event
     */
    void log(Transaction t, String event);
}
