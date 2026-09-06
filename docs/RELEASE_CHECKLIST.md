# Pilot Release Checklist

## Engineering
- [ ] Web CI green
- [ ] Android unit tests green
- [ ] Debug APK build green
- [ ] APK SHA-256 recorded
- [ ] No P0 defects
- [ ] Known limitations documented

## Security
- [ ] No secrets in repository
- [ ] Release signing is protected
- [ ] Permissions are least privilege
- [ ] Demo data contains no sensitive personal information
- [ ] Production APIs remain disabled until authenticated

## QA
- [ ] Install test completed on target Android device
- [ ] Camera test passed
- [ ] Media picker test passed
- [ ] Audio/video test passed where supported
- [ ] Offline test passed
- [ ] Biometric test passed or unavailable state documented
- [ ] App restart behavior recorded

## Distribution
- [ ] Release tag created
- [ ] Draft GitHub Release created
- [ ] APK and SHA256SUMS attached
- [ ] Release notes published
- [ ] Pilot warning included
- [ ] Tester feedback channel available
