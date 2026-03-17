package com.fintech.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteValidatorTest {
    private Transaction createTransaction(BigDecimal amount, String token) {
        TransactionRequest request = new TransactionRequest(amount, "USD", token, "MOCK");
        return new Transaction("TXN_123", request);
    }

    @Test
    void testSecurityValidator() {
        SecurityValidator validator = new SecurityValidator();
        
        // Pass case
        assertDoesNotThrow(() -> validator.validate(createTransaction(new BigDecimal("100"), "valid_token_long")));
        
        // Fail cases
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("100"), null)));
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("100"), "")));
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("100"), "short")));
    }

    @Test
    void testFraudCheckValidator() {
        FraudCheckValidator validator = new FraudCheckValidator();
        
        // Pass case
        assertDoesNotThrow(() -> validator.validate(createTransaction(new BigDecimal("500"), "token12345")));
        
        // Fail cases
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("-10"), "token12345")));
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("20000"), "token12345")));
    }

    @Test
    void testBalanceValidator() {
        BalanceValidator validator = new BalanceValidator();
        String token = "user_token_123";
        validator.setBalance(token, new BigDecimal("1000.00"));
        
        // Pass case
        assertDoesNotThrow(() -> validator.validate(createTransaction(new BigDecimal("500"), token)));
        
        // Fail case (insufficient)
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("1500"), token)));
        
        // Fail case (missing token/zero balance)
        assertThrows(ValidationException.class, () -> validator.validate(createTransaction(new BigDecimal("10"), "unknown_token")));
    }
}
