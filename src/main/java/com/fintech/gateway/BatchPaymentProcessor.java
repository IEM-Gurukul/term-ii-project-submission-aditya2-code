package com.fintech.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Handles batch processing of multiple payment requests concurrently.
 * Uses a fixed thread pool and returns results as a list of Futures.
 */
public class BatchPaymentProcessor {
    private final ExecutorService executorService;
    private final PaymentProcessor processor;

    public BatchPaymentProcessor(int poolSize, PaymentProcessor processor) {
        this.executorService = Executors.newFixedThreadPool(poolSize, new NamedThreadFactory("BatchProcessor"));
        this.processor = processor;
    }

    /**
     * Submits a batch of transaction requests for concurrent processing.
     * 
     * @param requests the list of requests to process
     * @return a list of Futures representing the pending results
     */
    public List<Future<TransactionResult>> processBatch(List<TransactionRequest> requests) {
        List<Future<TransactionResult>> futures = new ArrayList<>();
        for (TransactionRequest request : requests) {
            futures.add(executorService.submit(() -> processor.process(request)));
        }
        return futures;
    }

    /**
     * Shuts down the executor service.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
