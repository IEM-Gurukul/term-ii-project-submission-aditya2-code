package com.fintech.gateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {
    private BatchPaymentProcessor batchProcessor;
    private BalanceValidator balanceValidator;
    private String token = "concurrent_user_token";

    @BeforeEach
    void setUp() {
        ValidationChain chain = new ValidationChain();
        balanceValidator = new BalanceValidator();
        balanceValidator.setBalance(token, new BigDecimal("5000.00"));
        chain.addStep(balanceValidator);

        PaymentProcessor processor = new PaymentProcessor(chain, new MockStripeGateway(), null);
        batchProcessor = new BatchPaymentProcessor(5, processor);
    }

    @AfterEach
    void tearDown() {
        batchProcessor.shutdown();
    }

    @Test
    void testConcurrentBatchProcessing() throws Exception {
        int batchSize = 20;
        List<TransactionRequest> requests = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            requests.add(new TransactionRequest(new BigDecimal("10.00"), "USD", token, "STRIPE"));
        }

        List<Future<TransactionResult>> futures = batchProcessor.processBatch(requests);

        assertEquals(batchSize, futures.size());

        for (Future<TransactionResult> future : futures) {
            TransactionResult result = future.get();
            assertEquals(TransactionStatus.APPROVED, result.getStatus());
        }
    }
}
