# Mission Creation Guide

## Mission model
A mission describes the impact objective, its organisation, milestones and evidence requirements.

## Pilot flow
1. Create a mission draft.
2. Define the intended beneficiaries and measurable outcome.
3. Add milestones in a logical sequence.
4. Define evidence needed for each milestone.
5. Capture evidence against the relevant milestone.
6. Prepare the mission for human review.

## Quality rules
- State the problem and intended outcome clearly.
- Make milestones measurable where possible.
- Avoid claiming impact before evidence is reviewed.
- Keep evidence requirements proportionate to the claim.
- Record uncertainty rather than inventing precision.

## Future backend state
`DRAFT → ACTIVE → EVIDENCE_COLLECTION → UNDER_REVIEW → VERIFIED → COMPLETED`

The current Android pilot creates local drafts only; production persistence requires authenticated APIs and tenant isolation.
