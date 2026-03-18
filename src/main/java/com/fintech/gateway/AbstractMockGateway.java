package com.fintech.gateway;

import java.util.Random;

/**
 * Base class for mock gateways providing common simulation logic.
 */
public abstract class AbstractMockGateway implements PaymentGateway {
    private final Random random = new Random();
    private double failureRate = 0.1; // 10% chance of timeout by default

    public void setFailureRate(double failureRate) {
        this.failureRate = failureRate;
    }

    protected void simulateTimeout() throws GatewayTimeoutException {
        if (random.nextDouble() < failureRate) {
            throw new GatewayTimeoutException("Gateway connection timed out (Simulated)");
        }
    }
}
