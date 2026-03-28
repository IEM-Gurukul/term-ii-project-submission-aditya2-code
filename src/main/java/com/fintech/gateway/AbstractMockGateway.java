package com.fintech.gateway;

import java.util.Random;

/**
 * Base class for mock gateways providing common simulation logic.
 */
public abstract class AbstractMockGateway implements PaymentGateway {
    private final Random random = new Random();
    private double failureRate = 0.1; // 10% chance of timeout by default

    /**
     * Sets the simulated failure rate for this gateway.
     * 
     * @param failureRate a value between 0.0 (no failure) and 1.0 (always fail)
     */
    public void setFailureRate(double failureRate) {
        this.failureRate = failureRate;
    }

    /**
     * Simulates a gateway timeout based on the configured failure rate.
     * 
     * @throws GatewayTimeoutException if the simulated failure occurs
     */
    protected void simulateTimeout() throws GatewayTimeoutException {
        if (random.nextDouble() < failureRate) {
            throw new GatewayTimeoutException("Gateway connection timed out (Simulated)");
        }
    }
}
