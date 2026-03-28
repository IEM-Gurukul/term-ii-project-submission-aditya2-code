[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)
# PCCCS495 – Term II Project

## Project Title
FinTech Payment Gateway Pipeline

---

## Problem Statement (max 150 words)
Modern financial systems require robust, secure, and scalable payment processing engines. The challenge is to build a middle-ware orchestration layer that can validate transactions against security and fraud rules, coordinate with multiple third-party payment providers (gateways) while handling transient network failures, and manage high-volume concurrent traffic without data corruption. This project solves these issues by implementing a modular validation pipeline, an abstraction layer for payment gateways with automatic retry logic using exponential back-off, and a thread-safe batch processor for parallel execution. It ensures data privacy through automated sensitive field masking and maintains a durable, immutable audit trail for every transaction state transition, providing a reliable foundation for FinTech applications.

---

## Target User
- FinTech Developers needing a reusable payment orchestration library.
- Financial Institutions looking for a secure, multi-gateway integration layer.
- Systems Architects designing high-throughput transaction processing systems.

---

## Core Features

- **Validation Pipeline**: A modular "Chain of Responsibility" for security, fraud, and balance checks.
- **Resilient Gateway Integration**: Multi-provider support (Stripe/PayPal) with automated exponential back-off retries.
- **Concurrent Batch Processing**: High-performance execution of multiple transactions using thread pools.
- **Secure Auditing**: Immutable pipe-delimited logs with automatic masking of sensitive payment tokens.

---

## OOP Concepts Used

- **Abstraction**: Defined through `ValidationStep`, `PaymentGateway`, and `AuditLogger` interfaces to decouple logic from implementation.
- **Inheritance**: Utilized in the exception hierarchy (`PaymentException` base class) and mock gateway implementations (`AbstractMockGateway`).
- **Polymorphism**: Dynamic runtime switching of payment gateways (Strategy Pattern) and validation steps.
- **Exception Handling**: A custom granular hierarchy distinguishes between terminal validation errors and recoverable gateway timeouts.
- **Collections / Threads**: Uses `ConcurrentHashMap` for thread-safe ledger management and `ExecutorService` with a custom `ThreadFactory` for batch processing.

---

## Proposed Architecture Description
The project follows a **Pipeline Architecture** combined with the **Strategy** and **Chain of Responsibility** design patterns.
1. **Domain Layer**: Contains immutable DTOs for requests/results and a stateful `Transaction` entity.
2. **Validation Layer**: An ordered chain of steps that must all pass before a transaction proceeds.
3. **Orchestration Layer**: The `PaymentProcessor` coordinates the validation, retry logic via `RetryHandler`, and gateway execution.
4. **Concurrency Layer**: `BatchPaymentProcessor` leverages a fixed thread pool to resolve batches of transactions asynchronously using `Futures`.
5. **Persistence/Audit Layer**: Decoupled loggers that ensure every state transition is recorded for compliance.

---

## How to Run
Navigate to the `src/` directory and use the Gradle wrapper:

### Build
```powershell
.\gradlew.bat clean build
```

### Run Demo
```powershell
.\gradlew.bat run
```

### Run Tests
```powershell
.\gradlew.bat test
```

---

## Git Discipline Notes
Minimum 10 meaningful commits required. (Total commits: 12+)
- Phase-wise implementation from Skeleton to Concurrency.
- Meaningful commit messages following conventional commits standard.
