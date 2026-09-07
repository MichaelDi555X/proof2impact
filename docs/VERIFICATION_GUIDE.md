# Verification Guide

## Principle
Verification is an independent human decision supported by evidence, provenance and deterministic rules. AI may explain or flag gaps but must never be treated as an authenticity, legal or funding authority.

## Future review sequence
1. Authenticate reviewer.
2. Resolve organisation and role.
3. Load the evidence version and provenance.
4. Check reviewer conflict of interest.
5. Review evidence against milestone requirements.
6. Request clarification or record a decision.
7. Write an immutable audit event.
8. Recompute impact readiness.

## Pilot status
The current Android build only prepares a local review. It does not issue a production verification decision.

## Required backend states
`PENDING → CLAIMED → UNDER_REVIEW → CHANGES_REQUESTED | VERIFIED | REJECTED`

A verifier must not approve evidence they submitted themselves. Every decision must be attributable to an authenticated actor and organisation context.
