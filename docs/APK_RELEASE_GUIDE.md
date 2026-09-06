# APK Release Guide

## Debug pilot
The `Android Pilot` workflow builds and tests the debug APK on pull requests and feature branches. The artifact is evidence of a reproducible CI build, not a production-signed release.

## Signed pilot
Create a protected version tag only after release gates pass. CI must obtain signing values from protected secrets and a keystore supplied outside source control.

## Required release assets
- `Proof2Impact-<version>-<commit>.apk`
- `SHA256SUMS`
- release notes
- pilot user guide
- tester guide

GitHub Releases are tag-based and can package binary assets alongside release notes. Keep the release draft until all assets and validation evidence are present.

## Installation
Enable installation from the source used by the tester, verify the checksum, install, and record Android version/device model/build number in the test report.

## Rollback
If a pilot build is unsafe or unusable, unpublish the affected release or mark it withdrawn and direct testers to the previous validated build. Never silently replace an APK while retaining the same version identity.
