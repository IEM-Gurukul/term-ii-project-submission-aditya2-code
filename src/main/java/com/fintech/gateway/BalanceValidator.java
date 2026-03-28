package com.fintech.gateway;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements balance validation for transactions.
 * Uses a thread-safe ledger to track available funds.
 */
public class BalanceValidator implements ValidationStep {
    private final Map<String, BigDecimal> ledger = new ConcurrentHashMap<>();

    /**
     * Manually seeds a balance for a given payment token in the ledger.
     * 
     * @param token the payment token to associate with the balance
     * @param balance the amount to set in the ledger
     */
    public void setBalance(String token, BigDecimal balance) {
        ledger.put(token, balance);
    }

    /**
     * Validates that the account associated with the payment token has sufficient funds.
     * 
     * @param t the transaction to validate
     * @throws ValidationException if funds are insufficient or the token is unknown
     */
    @Override
    public void validate(Transaction t) throws ValidationException {
        String token = t.getRequest().getPaymentToken();
        BigDecimal amount = t.getRequest().getAmount();

        BigDecimal balance = ledger.getOrDefault(token, BigDecimal.ZERO);

        if (balance.compareTo(amount) < 0) {
            throw new ValidationException("Insufficient funds in account");
        }
    }
}
