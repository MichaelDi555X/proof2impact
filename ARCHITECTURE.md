# Proof2Impact Architecture

## Architectural goal

Proof2Impact connects real-world problems to measurable, verifiable outcomes through:

`Problem → Mission → Funding → Milestone → Evidence → Verification → Settlement → Impact Passport`

The platform should remain a lean modular monolith until scale or organizational boundaries justify independent services.

## Current implementation

- Next.js web application
- TypeScript
- Vercel deployment
- Server-side AI assistance route
- Deterministic readiness assessment fallback

## Target modular architecture

```text
apps/
  web/

packages/
  domain/
  auth/
  database/
  evidence/
  verification/
  funding/
  blockchain/
  observability/
  analytics/
  ai/

contracts/
docs/
.github/
```

## Bounded contexts

### Domain
Owns mission, milestone, impact metric and lifecycle rules.

### Auth
Owns identity, sessions, authorization and roles.

### Database
Owns persistence, migrations, transaction boundaries and repository adapters.

### Evidence
Owns evidence metadata, storage references, hashes, provenance and access policy.

### Verification
Owns verification cases, human decisions, confidence signals and disputes.

### Funding
Owns commitments, funding state, release conditions and settlement intent.

### Blockchain
Owns chain adapters, transaction submission, event processing and reconciliation. Business rules must not depend directly on a specific chain implementation.

### AI
Owns model access, prompt policy, structured outputs and safety validation. AI recommendations are never authoritative verification decisions.

### Observability
Owns structured logs, metrics, traces, correlation IDs and operational health signals.

## Security boundaries

- Browser clients never receive server secrets.
- Evidence is private by default.
- Authorization is enforced server-side.
- Verification decisions are attributable to authorized humans or explicitly governed automation.
- Blockchain transactions are reconciled from chain events rather than assumed successful from a client response.

## Blockchain strategy

Use a chain-neutral adapter with Arc and Base implementations. USDC and Circle integrations should be isolated behind explicit interfaces so the domain layer remains portable.

## Java future-service blueprint

If a future scale requirement warrants Java services, use Clean/Hexagonal Architecture:

```text
REST/Message Adapter
        ↓
Application Use Cases
        ↓
Domain Model
        ↓
Ports
        ↓
Infrastructure Adapters
```

Apply SOLID, immutable domain values, dependency injection, explicit transaction boundaries and idempotent command handling. Java services should not be introduced merely for architectural appearance.
