export const EVIDENCE_REQUIREMENTS = [
  "Organisation registration / legal identity",
  "Project or programme description",
  "Budget or funding request",
  "Evidence of impact / beneficiaries",
  "Contact and verification details",
] as const;

export type EvidenceRequirement = (typeof EVIDENCE_REQUIREMENTS)[number];

export interface ImpactAssessmentInput {
  project: string;
  impact: string;
  location: string;
  selectedEvidence: readonly string[];
}

export interface ImpactAssessmentResult {
  summary: string;
  readiness: number;
  gaps: string[];
  nextSteps: string[];
}

const clamp = (value: string, max: number) => value.trim().slice(0, max);

export function normalizeAssessmentInput(input: Partial<ImpactAssessmentInput>): ImpactAssessmentInput {
  return {
    project: clamp(input.project ?? "Impact project", 300) || "Impact project",
    impact: clamp(input.impact ?? "social impact", 160) || "social impact",
    location: clamp(input.location ?? "", 120),
    selectedEvidence: Array.isArray(input.selectedEvidence)
      ? [...new Set(input.selectedEvidence.filter((value): value is string => typeof value === "string").map((value) => clamp(value, 160)))].slice(0, 10)
      : [],
  };
}

export function assessImpact(input: ImpactAssessmentInput): ImpactAssessmentResult {
  const selected = new Set(input.selectedEvidence);
  const missing = EVIDENCE_REQUIREMENTS.filter((requirement) => !selected.has(requirement));
  const readiness = Math.round(((EVIDENCE_REQUIREMENTS.length - missing.length) / EVIDENCE_REQUIREMENTS.length) * 100);

  return {
    summary: `${input.project} is currently ${readiness}% documentation-ready for an initial ${input.impact} impact conversation. The result is a preparation aid, not a certification or funding decision.`,
    readiness,
    gaps: missing.length
      ? [...missing]
      : ["No checklist gaps identified; proceed to authenticity and eligibility review."],
    nextSteps: missing.length
      ? [
          "Collect the missing evidence and retain its source/provenance.",
          "Cross-check names, dates, figures and claims across documents.",
          "Prepare a concise impact brief with measurable outcomes and a realistic budget.",
        ]
      : [
          "Validate document authenticity and authority with the issuing sources.",
          "Prepare a concise impact brief with measurable outcomes and a realistic budget.",
          "Approach suitable funders or donors with evidence and reporting commitments.",
        ],
  };
}
