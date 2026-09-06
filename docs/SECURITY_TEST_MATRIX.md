# Security Test Matrix

## Standards

Proof2Impact uses OWASP MASVS/MAS testing guidance for the Android surface, OWASP ASVS for web/API controls, and NIST SSDF for secure development lifecycle evidence. NIST SP 800-218 SSDF 1.1 is final; NIST lists SP 800-218 Rev. 1 / SSDF 1.2 as a draft released in December 2025, so the project should track the draft without treating it as the final baseline. citeturn0search13turn0search0turn0search10

## Authentication

- session expiry and rotation
- secure cookie attributes
- CSRF protection where applicable
- OAuth callback validation
- device/session revocation
- brute-force/rate-limit controls
- MFA/passkey readiness

## Authorization

- tenant isolation
- object-level authorization on every resource ID
- RBAC permission matrix
- role downgrade/revocation
- verifier conflict-of-interest rule
- audit trail for authorization-sensitive mutations

## Evidence security

- allowlisted MIME types
- content sniffing protection
- bounded file size
- filename normalization
- private object storage
- quarantine before exposure
- malware scanning
- SHA-256 server-side verification
- signed short-lived access URLs
- no raw public evidence URLs
- duplicate/version handling

## API security

- schema validation
- bounded request bodies
- rate limiting
- idempotency for mutating commands
- consistent 4xx/5xx behavior
- safe error messages
- webhook signature verification
- replay protection
- dependency timeout/circuit behavior

## Mobile security

- no embedded API secrets
- Android Keystore for sensitive local secrets
- private app storage for queued evidence
- TLS only
- no cleartext traffic
- least-privilege permissions
- biometric protection for sensitive local actions
- no broad media/storage permissions unless a documented feature requires them
- no background location in baseline

## Supply chain

- pinned major toolchain versions
- dependency update automation
- lockfiles
- secret scanning
- SAST
- dependency vulnerability scanning
- SBOM for release candidates
- signed release artifact and checksum
- release provenance retained

## Penetration-test checklist

Before financial mainnet behavior: authentication bypass, IDOR/BOLA, tenant breakout, file upload attacks, SSRF, request smuggling, webhook replay, rate-limit bypass, privilege escalation, insecure direct object references, sensitive data exposure, mobile local-storage extraction, certificate/TLS misuse, and blockchain transaction authorization abuse.
