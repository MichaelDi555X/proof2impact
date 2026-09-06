# Android Setup

## Requirements
- Android Studio with JDK 17 support
- Android SDK 36
- Gradle 8.13 for CI parity
- Android device or emulator, API 26+

## Open
Open the repository root in Android Studio, then select the `android/` Gradle project.

## Build

```bash
cd android
gradle test
gradle :app:assembleDebug
```

The GitHub Actions workflow is the authoritative clean-build validation.

## Device testing
Enable Developer Options and USB debugging, connect the device, and install the generated debug APK. Test camera, media picker, microphone, video, optional location and biometric behavior individually.

## Privacy
No evidence is uploaded automatically by the pilot. Local evidence is stored in app-private storage. Do not use real beneficiary PII or sensitive documents during early demonstrations.

## Release signing
Release signing is intentionally unavailable unless protected CI signing secrets and the release keystore are supplied. Never commit a keystore or passwords.
