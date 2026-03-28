package com.fintech.gateway;

import java.math.BigDecimal;

/**
 * Immutable object representing a request for a payment transaction.
 */
public class TransactionRequest {
    private final BigDecimal amount;
    private final String currency;
    private final String paymentToken;
    private final String gatewayType;

    /**
     * Constructs a TransactionRequest.
     * @param amount the transaction amount
     * @param currency the ISO currency code
     * @param paymentToken the raw payment token or card reference
     * @param gatewayType the target payment gateway
     */
    public TransactionRequest(BigDecimal amount, String currency, String paymentToken, String gatewayType) {
        this.amount = amount;
        this.currency = currency;
        this.paymentToken = paymentToken;
        this.gatewayType = gatewayType;
    }

    /**
     * Gets the transaction amount.
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Gets the currency code.
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Gets the sensitive payment token.
     * @return the payment token
     */
    public String getPaymentToken() {
        return paymentToken;
    }

    /**
     * Gets the designated gateway type.
     * @return the gateway type
     */
    public String getGatewayType() {
        return gatewayType;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentToken='" + maskToken(paymentToken) + '\'' +
                ", gatewayType='" + gatewayType + '\'' +
                '}';
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 4) {
            return "****";
        }
        return "****" + token.substring(token.length() - 4);
    }
}
