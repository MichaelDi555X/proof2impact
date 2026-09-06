# Deployment Runbook

## Deployment model

Proof2Impact uses GitHub as the source of truth and Vercel for the Next.js web deployment.

Expected flow:

`feature branch → pull request → CI → Vercel Preview → verification → merge to main → Production`

## Repository layout

The Next.js application is currently under `web/`.

## Required quality gates

Before production:

1. `npm ci`
2. `npm run lint`
3. `npm run typecheck`
4. `npm run build`
5. Verify the Vercel Preview manually on a mobile device.
6. Confirm the preview contains the intended Git commit.
7. Confirm no secrets are present in source or logs.

## Production rule

Never treat a READY Vercel deployment as sufficient evidence that the correct application was released. Verify the production URL and commit association after deployment.

## Rollback

If production does not match the approved release, stop further feature work, identify the last known-good deployment, and roll back through Vercel's deployment controls. Record the incident and corrective action before resuming normal development.

## Environment variables

Production secrets belong in Vercel environment configuration. `.env.example` documents required variable names only; it must never contain real credentials.
