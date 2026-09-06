# Android Pilot Engineering Standard

## Product boundary

The Android app is local-first and evidence-centric. It may prepare missions, milestones, evidence and verification requests offline, but it must never display a local-only action as a server-confirmed verification, funding commitment or settlement.

## Permission strategy

| Capability | Manifest | Runtime | Pilot rule |
|---|---|---|---|
| Network | INTERNET | No | Required for authenticated sync |
| Camera | CAMERA | Yes | Request only when photo capture is selected |
| Microphone | RECORD_AUDIO | Yes | Request only when audio capture is selected |
| Existing photos/video | Prefer Photo Picker | Picker-managed | Do not request broad library access for basic selection |
| Approximate GPS | ACCESS_COARSE_LOCATION | Yes | Optional; capture only with consent |
| Precise GPS | ACCESS_FINE_LOCATION | Yes | Optional; justify as provenance feature |
| Notifications | POST_NOTIFICATIONS | Yes on supported Android versions | Ask only when useful for sync/review status |
| Biometrics | No manifest permission | System prompt | Use BiometricPrompt for sensitive local actions |
| Background location | Not requested | N/A | Explicitly out of pilot baseline |
| Broad storage | Not requested | N/A | Use app-private storage/SAF/Photo Picker |

## Runtime flow

1. Explain why the capability is needed.
2. Request permission immediately before the action.
3. If denied, keep the workflow usable and explain the reduced capability.
4. Never repeatedly prompt after permanent denial; provide a settings route where appropriate.
5. Record consent/provenance metadata only when the user has agreed.

## Evidence flow

`capture/select → validate → local private storage → SHA-256 → metadata → local queue → authenticated upload-init → upload → server finalize → server hash verification → provenance → human verification`

The mobile hash is an integrity signal. It is not proof of authenticity by itself.

## Offline-first rules

- UI reads local state first.
- No network response is treated as durable until server acknowledgement is received.
- Queue operations have stable client IDs and idempotency keys.
- Retry with bounded exponential backoff.
- Never delete local evidence because a network request failed.
- Never retry a rejected authorization request indefinitely.
- Large files use resumable/multipart upload on the production API.

## Privacy and consent copy requirements

Before collecting evidence, explain:

- what is collected;
- why it is collected;
- where it is stored locally;
- when it is uploaded;
- who may review it;
- whether GPS/audio is optional;
- how long it is retained;
- how a user can request deletion/export where applicable.

## Battery/data policy

Do not use continuous GPS, permanent foreground services, polling loops or unrestricted background work for the pilot. WorkManager is the durable synchronization boundary. Prefer Wi-Fi/mobile-data aware uploads, compressed previews and resumable transfers.

## Release-device baseline

Primary: Xiaomi Poco F6, Android 15/16-class environment.
Secondary: one lower-memory Android device representative of emerging-market conditions.

Required measurements: cold/warm startup, peak memory, APK size, CPU during upload, mobile-data bytes, battery impact, crash-free sessions, offline queue recovery and upload success after network restoration.
