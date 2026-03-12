# FinTech Payment Gateway Pipeline — Task Tracker

> **Stack:** Java · OOP · Design Patterns · Concurrency  
> **Version:** v1.0.0

---

## Phase 1 — Project Skeleton & Configuration

- [ ] Initialise Maven or Gradle build file with project metadata
- [ ] Define base package hierarchy (e.g. `com.fintech.gateway`)
- [ ] Add dependencies: JUnit 5, SLF4J / Logback (or `java.util.logging`)
- [ ] Create `.gitignore` and commit initial skeleton
- [ ] Verify project compiles clean from the command line

---

## Phase 2 — Domain Modeling

- [ ] Create `TransactionStatus` enum (`PENDING`, `APPROVED`, `DECLINED`, `FAILED`, `RETRYING`)
- [ ] Create `TransactionRequest` (amount, currency, paymentToken, gatewayType)
- [ ] Create `Transaction` (wraps request + assigned `transactionId` + mutable status)
- [ ] Create `TransactionResult` (transactionId, status, message, timestamp)
- [ ] Ensure all sensitive fields are private; expose via getters only
- [ ] Mask payment token in `toString()` output

---

## Phase 3 — Abstraction & Interface Design

- [ ] Define `ValidationStep` interface with `void validate(Transaction t) throws ValidationException`
- [ ] Define `PaymentGateway` interface with `TransactionResult process(Transaction t) throws GatewayTimeoutException`
- [ ] Define `AuditLogger` interface with `void log(Transaction t, String event)`
- [ ] Add Javadoc to every interface method

---

## Phase 4 — Validation Infrastructure (Chain of Responsibility)

- [ ] Create `ValidationChain` class with an ordered `ArrayList<ValidationStep>`
- [ ] Implement `addStep(ValidationStep)` registration method
- [ ] Implement `executeChain(Transaction)` — iterates steps, halts on first `ValidationException`
- [ ] Write unit test: chain halts after the first failing step

---

## Phase 5 — Concrete Validation Logic

- [ ] Implement `SecurityValidator` (token format / null checks)
- [ ] Implement `FraudCheckValidator` (velocity / threshold rules)
- [ ] Implement `BalanceValidator` (ledger lookup via `ConcurrentHashMap`)
- [ ] Each class implements `ValidationStep`
- [ ] Unit test each validator independently (pass + fail cases)

---

## Phase 6 — Gateway Strategy Implementation

- [ ] Implement `MockStripeGateway` implementing `PaymentGateway`
- [ ] Implement `MockPayPalGateway` implementing `PaymentGateway`
- [ ] Implement `MockCryptoGateway` implementing `PaymentGateway`
- [ ] Simulate random `GatewayTimeoutException` to enable retry testing
- [ ] Unit test each gateway mock in isolation

---

## Phase 7 — Orchestration Engine

- [ ] Create `PaymentProcessor` accepting `ValidationChain` and `PaymentGateway` via constructor injection
- [ ] Implement `process(TransactionRequest)` — build `Transaction`, run chain, invoke gateway, return `TransactionResult`
- [ ] Support runtime gateway switching (Strategy pattern — no `if/else` on type)
- [ ] Integration test: full happy-path and declined-path flows

---

## Phase 8 — Audit & Persistence Layer

- [ ] Implement `ConsoleAuditLogger` (writes to stdout)
- [ ] Implement `FileAuditLogger` (pipe-delimited append to `audit.log`)
- [ ] Log every state transition: `PENDING → APPROVED`, `PENDING → DECLINED`, etc.
- [ ] Ensure log entries are immutable (no post-write edits)
- [ ] Confirm payment token is masked in all log output

---

## Phase 9 — Exception Hierarchy

- [ ] Create abstract `PaymentException` (root; extends `Exception`)
- [ ] Create `ValidationException` extending `PaymentException` (non-recoverable)
- [ ] Create `GatewayTimeoutException` extending `PaymentException` (recoverable)
- [ ] Add typed `catch` blocks in `PaymentProcessor` — decline on `ValidationException`, retry on `GatewayTimeoutException`
- [ ] Unit test that correct exception type triggers correct recovery path

---

## Phase 10 — Retry Mechanism

- [ ] Implement `RetryHandler` (or embed in `PaymentProcessor`) with configurable `maxRetries`
- [ ] Apply exponential back-off (`wait = baseDelay * 2^attempt`)
- [ ] Fail-fast on `ValidationException` — zero retries
- [ ] Log each retry attempt via `AuditLogger`
- [ ] Unit test: succeeds on 3rd attempt; exhausts retries and returns `FAILED`

---

## Phase 11 — Concurrency & Threading

- [ ] Create `BatchPaymentProcessor` with `ExecutorService` fixed thread pool
- [ ] Implement `NamedThreadFactory` for readable thread names in logs
- [ ] Use `ConcurrentHashMap` for the balance ledger (thread-safe reads/writes)
- [ ] Collect `Future<TransactionResult>` in submission order; resolve after all complete
- [ ] Concurrency test: submit 10+ transactions simultaneously; assert no race conditions or lost results

---

## Phase 12 — Testing, Simulation & Documentation

- [ ] Write JUnit 5 test suite covering all success paths
- [ ] Write JUnit 5 test suite covering all failure / edge-case paths
- [ ] Add a `Main` / demo runner that exercises single and batch processing end-to-end
- [ ] Complete Javadoc on all public classes and methods
- [ ] Write `README.md` with architecture overview, setup steps, and usage examples
- [ ] Final review: no `System.out.println` outside demo/logger; no raw types; no unused imports
