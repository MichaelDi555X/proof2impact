# Proof2Impact QA Test Strategy

## Test pyramid

### Unit
- domain assessment rules
- normalization/clamping
- evidence hashing
- state transitions
- authorization policy decisions
- idempotency key handling

### Component
- Android permission states
- local evidence repository
- sync queue
- API handlers with mocked dependencies
- storage/quarantine adapter
- verification decision rules

### Integration
- PostgreSQL transaction boundaries
- RLS tenant isolation
- object storage upload/finalize
- malware scanner integration
- authentication provider
- webhook signature verification

### E2E
`sign in → organisation → mission → milestone → evidence → submit → verifier claim → approve/reject → funding commitment → testnet settlement simulation → Impact Passport`

## Critical negative cases

| ID | Scenario | Expected |
|---|---|---|
| SEC-01 | unauthenticated mission creation | 401/403; no row created |
| SEC-02 | member accesses another tenant resource | 403/404; no data disclosure |
| SEC-03 | verifier approves own evidence | blocked and audited |
| SEC-04 | forged/invalid webhook signature | rejected |
| SEC-05 | duplicate settlement request | one idempotent outcome |
| EVD-01 | camera denied | workflow continues without camera |
| EVD-02 | location denied | evidence saved without GPS; provenance records omission |
| EVD-03 | microphone denied | workflow continues without audio |
| EVD-04 | network loss during upload | local evidence retained; retry queued |
| EVD-05 | corrupted upload | finalize/hash verification fails; quarantined |
| EVD-06 | duplicate upload | duplicate detected or explicitly versioned |
| EVD-07 | oversized upload | rejected before persistence |
| EVD-08 | malicious upload | quarantine/scan failure; never exposed |
| AUTH-01 | expired session | re-authentication required |
| AUTH-02 | revoked device | subsequent API calls rejected |
| AUTH-03 | privilege downgrade | old elevated token cannot retain authority |

## Acceptance criteria

A feature is complete only when:

- happy path passes;
- denial/error path passes;
- authorization is tested;
- data persistence is tested;
- audit event is tested for mutations;
- retry/recovery behavior is tested where applicable;
- accessibility is checked;
- mobile low-bandwidth behavior is checked where applicable;
- no critical/high security defect remains.

## Pilot test-drive record

For each cycle record: issue, root cause, correction, retest result, risk reduction, user impact, business impact, engineering impact and prevention action.
