package com.fintech.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditLoggerTest {
    private Transaction transaction;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"), "USD", "super_secret_token_123", "STRIPE");
        transaction = new Transaction("TXN_AUDIT_001", request);
    }

    @Test
    void testConsoleAuditLogger() {
        ConsoleAuditLogger logger = new ConsoleAuditLogger();
        // Just verify it doesn't throw exceptions
        assertDoesNotThrow(() -> logger.log(transaction, "TEST_EVENT"));
    }

    @Test
    void testFileAuditLogger() throws IOException {
        Path logFile = tempDir.resolve("audit.log");
        FileAuditLogger logger = new FileAuditLogger(logFile.toString());

        logger.log(transaction, "INIT_PROCESS");
        transaction.setStatus(TransactionStatus.APPROVED);
        logger.log(transaction, "FINAL_STATUS");

        List<String> lines = Files.readAllLines(logFile);
        assertEquals(2, lines.size());
        
        // Verify token masking in file
        for (String line : lines) {
            assertTrue(line.contains("****_123"));
            assertFalse(line.contains("super_secret_token_123"));
            assertTrue(line.contains("TXN_AUDIT_001"));
        }
    }
}
