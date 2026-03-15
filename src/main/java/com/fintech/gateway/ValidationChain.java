package com.fintech.gateway;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates a series of ValidationStep instances in an ordered chain.
 * This implements the Chain of Responsibility pattern for transaction validation.
 */
public class ValidationChain {
    private final List<ValidationStep> steps = new ArrayList<>();

    /**
     * Registers a new validation step to the end of the chain.
     *
     * @param step the validation step to add
     */
    public void addStep(ValidationStep step) {
        if (step != null) {
            steps.add(step);
        }
    }

    /**
     * Executes all registered validation steps in order.
     * The process halts immediately upon the first ValidationException.
     *
     * @param t the transaction to validate
     * @throws ValidationException if any validation step fails
     */
    public void executeChain(Transaction t) throws ValidationException {
        for (ValidationStep step : steps) {
            step.validate(t);
        }
    }
}
