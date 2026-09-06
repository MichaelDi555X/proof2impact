# Proof2Impact Android Architecture

## Purpose
The Android pilot is an offline-first field evidence client for the Proof2Impact Evidence → Verification → Trust → Impact lifecycle. The pilot deliberately separates local capture from authenticated server submission.

## Architecture

`Compose UI → ViewModel → Use Case → Repository → Local/Remote Data Sources`

- **Presentation:** Jetpack Compose screens and immutable UI state.
- **ViewModel:** owns screen state, user actions and lifecycle-aware flows.
- **Domain:** use cases and business rules; no Android UI dependencies.
- **Data:** repositories coordinate local persistence and remote APIs.
- **Local:** app-private evidence files plus DataStore for small preferences. Room becomes the canonical queue when structured offline entities are introduced; Android guidance recommends Room for large/complex datasets and referential integrity. citeturn0search5
- **Sync:** WorkManager for deferrable authenticated synchronization, with constraints, retry and backoff. citeturn0search2turn0search8

## Package target

```text
org.proof2impact.app
├── presentation/
│   ├── navigation/
│   ├── welcome/
│   ├── organisation/
│   ├── mission/
│   ├── evidence/
│   ├── verification/
│   ├── impact/
│   ├── profile/
│   └── settings/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/
│   ├── local/
│   ├── remote/
│   ├── repository/
│   └── sync/
└── security/
```

## Pilot boundary

Implemented foundation: local evidence capture, SHA-256 integrity metadata, app-private persistence, permission-on-demand capture, WorkManager sync boundary and biometric readiness.

Not implemented: production authentication, tenant isolation, authenticated upload, malware scanning, server-side verification, funding and settlement.

## Dependency direction
Presentation depends on domain contracts. Data implements domain repository interfaces. Domain never depends on Compose, Activity or network clients. Android framework access is isolated to presentation/data/security adapters.

## Offline-first rule
A capture is successful only after the local evidence bytes and integrity metadata are durably stored. A queued sync is not an upload confirmation. Server acceptance must be represented by a separate synchronized state after authenticated API acknowledgement.
