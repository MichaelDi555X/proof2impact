# Testing Guide

## Test layers

### Unit
- Evidence MIME/source validation.
- SHA-256 determinism.
- Maximum evidence size.
- Domain readiness calculations.

### Instrumentation/UI
- App launches.
- Navigation destinations open.
- Permission denial leaves the app usable.
- Capture actions persist evidence.
- Biometric success/failure states are safe.

### Integration
- Authenticated API session creation.
- Evidence upload initiation.
- Upload completion and server hash comparison.
- Verification state transitions.
- Offline queue retry and deduplication.

### Release
- CI clean build.
- APK checksum.
- Install/launch on supported Android versions.
- No debug-only endpoints or secrets.

## Core acceptance cases

| ID | Scenario | Expected result |
|---|---|---|
| A-01 | Launch app offline | Pilot opens and local features remain usable |
| A-02 | Deny camera | App remains usable and explains the limitation |
| A-03 | Capture photo | Local file and SHA-256 metadata are created |
| A-04 | Select media | Selected media is streamed into app-private storage |
| A-05 | Oversized evidence | Capture fails safely without retaining partial evidence |
| A-06 | Schedule sync offline | Work is queued; UI does not claim upload success |
| A-07 | Biometric unavailable | Sensitive local action is rejected safely |
| A-08 | Verifier conflict | Production backend must reject self-review |
| A-09 | Network loss during sync | Work retries without duplicating accepted server state |
| A-10 | Release install | APK installs, launches and reports correct version |

## Regression gate
Every change touching evidence, permissions, navigation, authentication, sync or release configuration requires the relevant unit/instrumentation test plus a clean debug build.
