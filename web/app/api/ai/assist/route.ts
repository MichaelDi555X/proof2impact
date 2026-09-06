import { NextResponse } from "next/server";
import { assessImpact, normalizeAssessmentInput } from "@proof2impact/domain";

export const runtime = "edge";

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const input = normalizeAssessmentInput({
      project: body?.project,
      impact: body?.impact,
      location: body?.location,
      selectedEvidence: body?.selectedDocs,
    });
    const base = assessImpact(input);

    // Optional AI enhancement. Deterministic domain logic remains the safe baseline.
    if (process.env.AI_GATEWAY_API_KEY && process.env.AI_MODEL) {
      const response = await fetch("https://ai-gateway.vercel.sh/v1/chat/completions", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${process.env.AI_GATEWAY_API_KEY}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          model: process.env.AI_MODEL,
          messages: [
            {
              role: "system",
              content:
                "You are Proof2Impact's evidence-readiness assistant. Never claim that a document is authentic, an organisation is legally verified, or funding is guaranteed. Return concise JSON with summary, gaps array, and nextSteps array. Treat the deterministic baseline as authoritative for readiness calculation.",
            },
            {
              role: "user",
              content: JSON.stringify({ ...input, baseline: base }),
            },
          ],
          max_tokens: Number(process.env.AI_MAX_TOKENS ?? "500"),
        }),
      });

      if (response.ok) {
        const ai = await response.json();
        const text = ai?.choices?.[0]?.message?.content;
        if (typeof text === "string") {
          try {
            const parsed = JSON.parse(text);
            return NextResponse.json({
              ...base,
              summary: typeof parsed.summary === "string" ? parsed.summary.slice(0, 1000) : base.summary,
              gaps: Array.isArray(parsed.gaps) ? parsed.gaps.filter((value: unknown): value is string => typeof value === "string").slice(0, 10) : base.gaps,
              nextSteps: Array.isArray(parsed.nextSteps)
                ? parsed.nextSteps.filter((value: unknown): value is string => typeof value === "string").slice(0, 10)
                : base.nextSteps,
            });
          } catch {
            // Safely use the deterministic baseline when the model does not return valid JSON.
          }
        }
      }
    }

    return NextResponse.json(base);
  } catch {
    return NextResponse.json({ error: "Invalid assessment request." }, { status: 400 });
  }
}
