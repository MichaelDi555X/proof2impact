# ADR 0002: GitHub is the release source of truth

- Status: Accepted
- Date: 2026-09-06

## Context

A READY Vercel deployment can still be the wrong application version. Proof2Impact previously exhibited a production mismatch where the live domain did not correspond to the intended repository state.

## Decision

GitHub `main` is the production source of truth. Every change follows:

`feature branch → PR → CI → Vercel Preview → mobile verification → merge → Production`

Production verification must confirm both the expected application behavior and the deployed Git commit. The application health endpoint exposes the deployment commit identifier without exposing secrets.

## Consequences

- Preview deployments become the primary release candidate.
- CI blocks known quality regressions before merge.
- Deployment records are traceable to Git commits.
- Rollback uses a known-good Vercel deployment rather than an ad-hoc rebuild.
