# ADR 0001: Lean modular monolith

- Status: Accepted
- Date: 2026-09-06

## Context

Proof2Impact is being built by a solo founder with mobile-first tooling and limited infrastructure budget. The product needs strong domain boundaries, auditability and a path to scale without operating a distributed platform prematurely.

## Decision

Keep the platform as a modular monolith on Next.js/TypeScript/Vercel with managed PostgreSQL and object storage. Organize code by bounded context and enforce dependency direction through interfaces and server-side authorization.

Initial bounded contexts:

- domain
- auth
- database
- evidence
- verification
- funding
- blockchain
- ai
- observability
- analytics

Use asynchronous/event-driven processing only where it removes reliability or latency pressure. Introduce independent services only when scale, isolation, compliance, or team ownership makes the boundary economically justified.

## Consequences

Positive:

- low operational burden
- simple local and mobile development workflow
- clear domain ownership
- easier transactions across core workflows
- straightforward Vercel deployment

Trade-off:

- module boundaries must be enforced by convention and automated checks
- a later extraction into Java or other services requires stable contracts and event schemas

## Rejected alternative

Premature microservices/Kubernetes were rejected because they add operational complexity without current product value.
