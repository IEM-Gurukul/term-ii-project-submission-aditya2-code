package com.fintech.gateway;

import java.math.BigDecimal;

public class TransactionRequest {
    private final BigDecimal amount;
    private final String currency;
    private final String paymentToken;
    private final String gatewayType;

    public TransactionRequest(BigDecimal amount, String currency, String paymentToken, String gatewayType) {
        this.amount = amount;
        this.currency = currency;
        this.paymentToken = paymentToken;
        this.gatewayType = gatewayType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

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
