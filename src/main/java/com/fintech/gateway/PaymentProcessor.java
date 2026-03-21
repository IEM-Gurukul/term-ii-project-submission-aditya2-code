package com.fintech.gateway;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orchestrates the payment processing flow.
 * Runs validation steps, then delegates to a payment gateway.
 */
public class PaymentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);
    
    private final ValidationChain validationChain;
    private PaymentGateway gateway;
    private final AuditLogger auditLogger;

    public PaymentProcessor(ValidationChain validationChain, PaymentGateway gateway, AuditLogger auditLogger) {
        this.validationChain = validationChain;
        this.gateway = gateway;
        this.auditLogger = auditLogger;
    }

    /**
     * Sets the gateway at runtime (Strategy pattern).
     * @param gateway the new gateway to use
     */
    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * Processes a transaction request.
     * 
     * @param request the request to process
     * @return the result of the transaction
     */
    public TransactionResult process(TransactionRequest request) {
        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(transactionId, request);
        
        try {
            logger.info("Starting processing for transaction: {}", transactionId);
            if (auditLogger != null) auditLogger.log(transaction, "PENDING");

            // 1. Run Validation Chain
            validationChain.executeChain(transaction);
            
            // 2. Process via Gateway
            TransactionResult result = gateway.process(transaction);
            
            // 3. Update Status based on result
            transaction.setStatus(result.getStatus());
            if (auditLogger != null) auditLogger.log(transaction, result.getStatus().toString());
            
            return result;

        } catch (ValidationException e) {
            // Non-recoverable validation failure
            logger.error("Validation failed for transaction {}: {}", transactionId, e.getMessage());
            transaction.setStatus(TransactionStatus.DECLINED);
            if (auditLogger != null) auditLogger.log(transaction, "DECLINED: " + e.getMessage());
            return new TransactionResult(transactionId, TransactionStatus.DECLINED, e.getMessage());
            
        } catch (GatewayTimeoutException e) {
            // Recoverable gateway timeout - will be handled by retry mechanism in Phase 10
            logger.error("Gateway timeout for transaction {}: {}", transactionId, e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            if (auditLogger != null) auditLogger.log(transaction, "FAILED: Gateway timeout");
            return new TransactionResult(transactionId, TransactionStatus.FAILED, "Gateway timeout: " + e.getMessage());
        } catch (Exception e) {
            // Unexpected system error
            logger.error("Unexpected error for transaction {}: {}", transactionId, e.getMessage(), e);
            transaction.setStatus(TransactionStatus.FAILED);
            if (auditLogger != null) auditLogger.log(transaction, "FAILED: Internal error");
            return new TransactionResult(transactionId, TransactionStatus.FAILED, "Internal system error");
        }
    }
}
