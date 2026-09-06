# Observability Baseline

## Principles

Observability must remain useful to a solo founder: managed services, structured output and actionable alerts over complex telemetry infrastructure.

## Required signals

### Logs
Use structured server-side logs with:

- timestamp
- severity
- request/correlation ID
- route or operation
- deployment/version identifier
- outcome
- duration
- safe error category

Never log secrets, access tokens, private evidence contents or unnecessary personal data.

### Metrics
Track at minimum:

- request count
- error rate
- API latency
- AI latency
- AI fallback rate
- authentication failures
- evidence processing failures
- settlement transaction failures

### Health
Expose a lightweight application health signal that verifies application availability without revealing secrets or sensitive database details.

## Reliability patterns

- Explicit request timeouts
- Bounded retries only for transient operations
- Idempotency for commands that can cause side effects
- Graceful AI degradation to deterministic assessment where safe
- Circuit-breaker behavior around unreliable external services when integration volume warrants it

## Incident rule

Every production incident should record: detection, impact, timeline, root cause, corrective action and verification of the fix.
