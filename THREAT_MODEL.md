# Proof2Impact Threat Model

## Assets

- User accounts and sessions
- Organization membership and roles
- Mission and milestone data
- Evidence metadata and evidence files
- Verification decisions
- Funding commitments
- Blockchain transaction records
- Impact Passport records
- AI inputs and outputs
- Audit events
- Runtime secrets

## Primary threats

| Threat | Impact | Initial control |
|---|---|---|
| Account takeover | High | Secure OIDC/session design, MFA readiness |
| Broken access control | Critical | Server-side RBAC and object-level authorization |
| Evidence tampering | Critical | Immutable metadata, cryptographic hashes, provenance |
| Malicious uploads | High | Content validation, isolated storage, malware scanning |
| AI prompt injection | High | Input separation, policy validation, human authority |
| API abuse | Medium/High | Authentication, rate limits, quotas |
| Funding manipulation | Critical | Authorization, state machines, idempotency, audit events |
| Transaction replay/duplication | Critical | Nonces, idempotency keys, reconciliation |
| Smart-contract exploit | Critical | Tests, least privilege, pause controls, independent audit before mainnet |
| Privacy leakage | Critical | Private-by-default storage and object authorization |
| Incorrect deployment | High | CI, commit verification, preview testing, release records |

## CIA analysis

### Confidentiality
Evidence, personal data, credentials and private operational data must only be accessible to authorized principals.

### Integrity
Verification, funding and settlement state transitions require authorization, validation, audit events and deterministic state machines.

### Availability
Use managed/serverless infrastructure, bounded requests, timeouts, graceful degradation and observable health signals.

## AI safety boundary

AI can summarize, classify, suggest and explain. AI cannot independently certify evidence authenticity, legal status, funding entitlement or settlement authorization.

## Blockchain safety boundary

No mainnet financial deployment until smart contracts and transaction flows have undergone appropriate testing, threat review, operational controls and independent security review.
