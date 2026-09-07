# Evidence Capture Guide

## Capture policy
Capture only evidence necessary to support a mission milestone. Prefer original media, preserve timestamps where available, and avoid collecting unnecessary personal data.

## Supported pilot paths
- Camera photo
- System media picker
- External audio recorder
- External video capture
- Optional coarse/fine location for provenance context

## Integrity
The pilot streams evidence into app-private storage and calculates SHA-256 while writing. A successful local capture means the bytes and integrity metadata were stored locally; it does not mean the evidence has been verified.

## Recommended metadata
- mission ID
- milestone ID
- capture time
- source type
- MIME type
- file size
- SHA-256
- optional location with explicit consent
- future device/app provenance metadata

## Privacy
Location is optional. Camera, microphone and location are requested at the point of use. Testers should use synthetic or non-sensitive material.
