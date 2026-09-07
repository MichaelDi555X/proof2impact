# Pilot Release Plan

## Objective
Deliver a clearly labelled demonstration release proving the local Evidence → Verification → Impact experience without implying production verification or financial settlement.

## Release stages
1. **Internal build:** CI green, debug APK artifact, deterministic checksums.
2. **Founder device pilot:** install on a real Android device; exercise capture and offline behavior.
3. **Closed tester pilot:** 3–10 trusted testers using synthetic/demo evidence.
4. **Demonstration release:** publish only after acceptance criteria are met.
5. **Production MVP:** requires authentication, tenancy, backend evidence pipeline, verification, auditability, monitoring and release signing.

## Exit criteria
- Web CI green.
- Android unit tests green.
- Debug APK generated and checksum recorded.
- No known P0 security issue.
- All pilot limitations documented.
- Demo data contains no real sensitive beneficiary information.
- Release notes explicitly identify the build as pilot/demo software.

## Non-goals
No real-money settlement, public verification claims, production identity assurance, or automatic authenticity certification.
