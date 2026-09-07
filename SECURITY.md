# Security Policy

## Scope

Proof2Impact is an early-stage impact verification and funding platform. The current web application is a prototype and must not be treated as a production financial, legal, identity, or evidence-certification system.

## Security principles

- Never commit credentials, private keys, API keys, or production secrets.
- Evidence is private by default and must require explicit authorization before access.
- AI assists users but does not certify authenticity, legal status, verification, or funding eligibility.
- Human review remains authoritative for verification decisions.
- Financial settlement must be testnet-only until contracts, infrastructure, compliance, and security reviews are complete.
- Every security-sensitive state transition should be auditable.

## Reporting

For a vulnerability, do not disclose exploitable details publicly before a fix is available. Report the issue privately to the project maintainer through an appropriate private GitHub security channel when available.

## Development requirements

All pull requests should pass linting, TypeScript checks, and a production build before merge. New security-sensitive functionality must include tests and documentation.

## Secret management

Use Vercel environment variables or another managed secret store for runtime secrets. Keep `.env` files out of Git. Never place wallet private keys or signing credentials in source code.
