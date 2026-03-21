package com.fintech.gateway;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of AuditLogger that appends events to a file.
 * Log entries are pipe-delimited and immutable once written.
 */
public class FileAuditLogger implements AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger(FileAuditLogger.class);
    private final String filePath;

    public FileAuditLogger(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void log(Transaction t, String event) {
        String timestamp = LocalDateTime.now().toString();
        String maskedToken = t.getRequest().getPaymentToken();
        if (maskedToken != null && maskedToken.length() > 4) {
            maskedToken = "****" + maskedToken.substring(maskedToken.length() - 4);
        } else {
            maskedToken = "****";
        }

        String logEntry = String.format("%s|%s|%s|%s|%s",
                timestamp, t.getTransactionId(), t.getStatus(), event, maskedToken);

        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logEntry);
        } catch (IOException e) {
            logger.error("Failed to write to audit log file: {}", filePath, e);
        }
    }
}
