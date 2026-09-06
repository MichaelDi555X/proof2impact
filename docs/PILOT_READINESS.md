# Proof2Impact Pilot Readiness

Date: 2026-09-06

## Release decision

**NOT READY for an external production pilot yet.** The repository now contains an Android pilot foundation and automated APK build path, but the signed APK, backend identity/tenancy, PostgreSQL persistence, evidence upload service, human verification service, funding state machine, settlement adapters, and production promotion gate are not implemented end-to-end.

## Implemented in this release branch

- Android Kotlin/Compose pilot project skeleton.
- Least-privilege manifest: network, camera, microphone, coarse/fine location and notifications only.
- Photo Picker path for existing media.
- Local evidence file persistence with SHA-256 hashing and a 50 MiB per-item pilot limit.
- WorkManager network-constrained sync boundary; it deliberately does not claim upload success.
- Mission/milestone/verification pilot UI states with explicit server-pending language.
- Android unit test baseline.
- GitHub Actions debug APK build and checksum artifact.
- Tag-triggered signed release candidate workflow gated by protected signing secrets.
- Web pilot badge corrected so the UI does not imply production status.
- AI model selection moved to explicit `AI_MODEL` configuration; no hard-coded model is assumed.

## P0 blockers before external pilot

1. Merge only after CI is green and PR review is complete.
2. Production authentication and secure session management.
3. Organisation tenancy, membership, RBAC and object-level authorization.
4. PostgreSQL schema, migrations, transactions and RLS.
5. Evidence upload-init/finalize API with private object storage.
6. Server-side MIME/size validation, quarantine and malware scanning.
7. Evidence provenance, immutable hashes and audit events.
8. Human verification workflow and conflict-of-interest controls.
9. Authenticated Android API client with idempotent offline synchronization.
10. Production readiness endpoint and deployment-SHA verification.
11. Signed release keystore stored only as GitHub Actions protected secrets/environment.
12. Device validation on the Poco F6 and at least one lower-end Android device.

## P1 pilot hardening

- rate limiting and abuse controls
- structured API schemas and negative tests
- crash/error telemetry
- backup/restore drill
- privacy policy and consent flows
- accessibility review
- battery/data-use measurements
- malicious/corrupt/duplicate/oversized evidence tests
- network interruption and retry tests
- independent security review before financial functionality

## Required environment inventory

### Web/backend
`AUTH_SECRET`, `AUTH_URL`, provider client secrets, `DATABASE_URL`, `DATABASE_DIRECT_URL`, object-storage credentials, `EVIDENCE_MAX_BYTES`, `APP_BASE_URL`, email credentials, webhook signing secret, AI gateway credentials/model, observability credentials.

### Blockchain testnet
`ARC_TESTNET_RPC_URL`, `ARC_CHAIN_ID`, `ARC_USDC_ADDRESS`, `BASE_SEPOLIA_RPC_URL`, `BASE_SEPOLIA_CHAIN_ID`, `BASE_SEPOLIA_USDC_ADDRESS`.

No mainnet signing key belongs in the mobile application or source repository.

## Pilot exit criteria

A pilot is approved only when:

- all P0 blockers are green;
- CI, unit, integration, API and E2E suites are green;
- a signed APK checksum matches the published artifact;
- authentication and authorization negative tests pass;
- evidence can be captured offline, queued, uploaded, hashed, verified and audited;
- production `/api/health` reports the exact release commit;
- rollback has been rehearsed;
- no known critical/high security defect remains open;
- pilot participants receive explicit beta/testnet limitations and data-use disclosures.
