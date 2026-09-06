# Contributing

## Development model

Use short-lived feature branches and pull requests. `main` is the production branch.

## Required checks

Every change should pass:

```text
npm ci
npm run lint
npm run typecheck
npm run build
```

The CI workflow is the source of truth for automated quality gates.

## Pull requests

A pull request should explain:

- problem being solved
- user impact
- security/privacy impact
- tests performed
- deployment impact
- migration or rollback considerations

## Architecture rules

- Prefer the simplest design that satisfies the requirement.
- Keep domain rules independent from UI and infrastructure.
- Validate external input at system boundaries.
- Keep authorization server-side.
- Do not commit secrets.
- Do not add blockchain mainnet functionality without explicit security review.
- AI must remain advisory unless a future governance decision explicitly establishes otherwise.

## Database changes

Schema changes must be backward-compatible where practical, include migrations and define rollback considerations before production deployment.
