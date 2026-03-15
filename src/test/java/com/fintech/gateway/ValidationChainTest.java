package com.fintech.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ValidationChainTest {
    private ValidationChain chain;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        chain = new ValidationChain();
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"), "USD", "token_123", "STRIPE");
        transaction = new Transaction("TXN_001", request);
    }

    @Test
    void testChainHaltsOnFirstFailure() {
        AtomicInteger callCount = new AtomicInteger(0);

        // Step 1: Success
        chain.addStep(t -> callCount.incrementAndGet());

        // Step 2: Failure
        chain.addStep(t -> {
            callCount.incrementAndGet();
            throw new ValidationException("Step 2 failed");
        });

        // Step 3: Should not be reached
        chain.addStep(t -> callCount.incrementAndGet());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            chain.executeChain(transaction);
        });

        assertEquals("Step 2 failed", exception.getMessage());
        assertEquals(2, callCount.get(), "Chain should have halted after the second step");
    }

    @Test
    void testEmptyChainSucceeds() throws ValidationException {
        assertDoesNotThrow(() -> chain.executeChain(transaction));
    }

    @Test
    void testAllStepsSucceed() throws ValidationException {
        AtomicInteger callCount = new AtomicInteger(0);
        chain.addStep(t -> callCount.incrementAndGet());
        chain.addStep(t -> callCount.incrementAndGet());

        chain.executeChain(transaction);
        assertEquals(2, callCount.get());
    }
}
