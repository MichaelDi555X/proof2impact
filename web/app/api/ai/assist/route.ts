import { NextResponse } from "next/server";

export const runtime = "edge";

function localAssessment(selectedDocs: string[], impact: string, project: string) {
  const all = [
    "Organisation registration / legal identity",
    "Project or programme description",
    "Budget or funding request",
    "Evidence of impact / beneficiaries",
    "Contact and verification details",
  ];
  const missing = all.filter((x) => !selectedDocs.includes(x));
  const readiness = Math.round((selectedDocs.length / all.length) * 100);
  return {
    summary: `${project} is currently ${readiness}% documentation-ready for an initial ${impact} impact conversation. The result is a preparation aid, not a certification or funding decision.`,
    readiness,
    gaps: missing.length ? missing : ["No checklist gaps identified; proceed to authenticity and eligibility review."],
    nextSteps: missing.length
      ? ["Collect the missing evidence and retain its source/provenance.", "Cross-check names, dates, figures and claims across documents.", "Prepare a concise impact brief with measurable outcomes and a realistic budget."]
      : ["Validate document authenticity and authority with the issuing sources.", "Prepare a concise impact brief with measurable outcomes and a realistic budget.", "Approach suitable funders or donors with evidence and reporting commitments."],
  };
}

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const project = String(body.project || "Impact project").slice(0, 300);
    const impact = String(body.impact || "social impact").slice(0, 160);
    const location = String(body.location || "").slice(0, 120);
    const selectedDocs = Array.isArray(body.selectedDocs) ? body.selectedDocs.map(String).slice(0, 10) : [];
    const base = localAssessment(selectedDocs, impact, project);

    // Optional Vercel AI Gateway enhancement. The app remains useful without a key.
    if (process.env.AI_GATEWAY_API_KEY) {
      const response = await fetch("https://ai-gateway.vercel.sh/v1/chat/completions", {
        method: "POST",
        headers: { Authorization: `Bearer ${process.env.AI_GATEWAY_API_KEY}`, "Content-Type": "application/json" },
        body: JSON.stringify({
          model: "openai/gpt-5.6-sol",
          messages: [{ role: "system", content: "You are Proof2Impact's evidence-readiness assistant. Never claim that a document is authentic, an organisation is legally verified, or funding is guaranteed. Return concise JSON with summary, gaps array, and nextSteps array." }, { role: "user", content: JSON.stringify({ project, location, impact, selectedDocs, baseline: base }) }],
          max_tokens: 500,
        }),
      });
      if (response.ok) {
        const ai = await response.json();
        const text = ai?.choices?.[0]?.message?.content;
        if (typeof text === "string") {
          try {
            const parsed = JSON.parse(text);
            return NextResponse.json({ ...base, summary: parsed.summary || base.summary, gaps: Array.isArray(parsed.gaps) ? parsed.gaps : base.gaps, nextSteps: Array.isArray(parsed.nextSteps) ? parsed.nextSteps : base.nextSteps });
          } catch { /* safely use deterministic baseline */ }
        }
      }
    }
    return NextResponse.json(base);
  } catch {
    return NextResponse.json({ error: "Invalid assessment request." }, { status: 400 });
  }
}
