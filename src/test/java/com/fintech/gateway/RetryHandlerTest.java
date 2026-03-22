package com.fintech.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryHandlerTest {
    private ValidationChain chain;
    private List<String> auditLogs;
    private AuditLogger auditLogger;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        chain = new ValidationChain();
        auditLogs = new ArrayList<>();
        auditLogger = (t, event) -> auditLogs.add(event);
        request = new TransactionRequest(new BigDecimal("100.00"), "USD", "valid_token", "MOCK");
    }

    @Test
    void testRetrySucceedsOnThirdAttempt() {
        AtomicInteger attempts = new AtomicInteger(0);
        PaymentGateway unstableGateway = t -> {
            if (attempts.incrementAndGet() < 3) {
                throw new GatewayTimeoutException("Timeout");
            }
            return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "Success after retries");
        };

        RetryHandler retryHandler = new RetryHandler(3, 10);
        PaymentProcessor processor = new PaymentProcessor(chain, unstableGateway, auditLogger, retryHandler);

        TransactionResult result = processor.process(request);

        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertEquals(3, attempts.get());
        assertTrue(auditLogs.stream().anyMatch(log -> log.contains("RETRY_ATTEMPT_1")));
        assertTrue(auditLogs.stream().anyMatch(log -> log.contains("RETRY_ATTEMPT_2")));
    }

    @Test
    void testRetryExhaustion() {
        PaymentGateway failingGateway = t -> {
            throw new GatewayTimeoutException("Always timeout");
        };

        RetryHandler retryHandler = new RetryHandler(2, 10);
        PaymentProcessor processor = new PaymentProcessor(chain, failingGateway, auditLogger, retryHandler);

        TransactionResult result = processor.process(request);

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertTrue(result.getMessage().contains("Gateway timeout"));
        // 1 initial + 2 retries = 3 attempts total is not how this is implemented. 
        // Logic: 1st call fails -> retry 1 -> fails -> retry 2 -> fails -> throw.
        assertTrue(auditLogs.stream().anyMatch(log -> log.contains("RETRY_ATTEMPT_1")));
        assertTrue(auditLogs.stream().anyMatch(log -> log.contains("RETRY_ATTEMPT_2")));
    }

    @Test
    void testFailFastOnValidationException() {
        chain.addStep(t -> { throw new ValidationException("Invalid"); });
        
        AtomicInteger gatewayCalls = new AtomicInteger(0);
        PaymentGateway gateway = t -> {
            gatewayCalls.incrementAndGet();
            return new TransactionResult(t.getTransactionId(), TransactionStatus.APPROVED, "OK");
        };

        RetryHandler retryHandler = new RetryHandler(3, 10);
        PaymentProcessor processor = new PaymentProcessor(chain, gateway, auditLogger, retryHandler);

        TransactionResult result = processor.process(request);

        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertEquals(0, gatewayCalls.get(), "Gateway should never be called if validation fails");
    }
}
