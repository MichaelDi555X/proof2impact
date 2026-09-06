import { NextResponse } from "next/server";
import { assessImpact, normalizeAssessmentInput } from "@proof2impact/domain";

export const runtime = "edge";

const MAX_REQUEST_CHARS = 32_000;
const MAX_AI_TOKENS = 1_000;

function errorResponse(message: string, status: number) {
  return NextResponse.json({ error: message }, { status });
}

export async function POST(request: Request) {
  try {
    const declaredLength = request.headers.get("content-length");
    if (declaredLength && Number(declaredLength) > MAX_REQUEST_CHARS) {
      return errorResponse("Assessment request is too large.", 413);
    }

    const rawBody = await request.text();
    if (rawBody.length > MAX_REQUEST_CHARS) {
      return errorResponse("Assessment request is too large.", 413);
    }

    const body: unknown = JSON.parse(rawBody);
    if (!body || typeof body !== "object" || Array.isArray(body)) {
      return errorResponse("Invalid assessment request.", 400);
    }

    const candidate = body as Record<string, unknown>;
    const selectedDocs = Array.isArray(candidate.selectedDocs)
      ? candidate.selectedDocs.filter((value): value is string => typeof value === "string").slice(0, 10)
      : [];

    const input = normalizeAssessmentInput({
      project: typeof candidate.project === "string" ? candidate.project : undefined,
      impact: typeof candidate.impact === "string" ? candidate.impact : undefined,
      location: typeof candidate.location === "string" ? candidate.location : undefined,
      selectedEvidence: selectedDocs,
    });
    const base = assessImpact(input);

    // AI is opt-in. Keep it disabled by default until authenticated access and rate limiting are enabled.
    const aiEnabled = process.env.AI_ASSIST_ENABLED === "true";
    if (aiEnabled && process.env.AI_GATEWAY_API_KEY && process.env.AI_MODEL) {
      const configuredTokens = Number(process.env.AI_MAX_TOKENS ?? "500");
      const maxTokens = Number.isFinite(configuredTokens)
        ? Math.max(100, Math.min(MAX_AI_TOKENS, Math.trunc(configuredTokens)))
        : 500;
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 8_000);

      try {
        try {
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
              max_tokens: maxTokens,
            }),
            signal: controller.signal,
          });

          if (response.ok) {
            const ai: unknown = await response.json();
            const text =
              ai && typeof ai === "object" && !Array.isArray(ai)
                ? (ai as { choices?: Array<{ message?: { content?: unknown } }> }).choices?.[0]?.message?.content
                : undefined;
            if (typeof text === "string") {
              try {
                const parsed: unknown = JSON.parse(text);
                if (parsed && typeof parsed === "object" && !Array.isArray(parsed)) {
                  const result = parsed as Record<string, unknown>;
                  return NextResponse.json({
                    ...base,
                    summary: typeof result.summary === "string" ? result.summary.slice(0, 1000) : base.summary,
                    gaps: Array.isArray(result.gaps)
                      ? result.gaps.filter((value): value is string => typeof value === "string").slice(0, 10)
                      : base.gaps,
                    nextSteps: Array.isArray(result.nextSteps)
                      ? result.nextSteps.filter((value): value is string => typeof value === "string").slice(0, 10)
                      : base.nextSteps,
                  });
                }
              } catch {
                // Safely use the deterministic baseline when the model does not return valid JSON.
              }
            }
          }
        } catch {
          // External AI failure, timeout or malformed response must never break the deterministic assessment.
        }
      } finally {
        clearTimeout(timeout);
      }
    }

    return NextResponse.json(base);
  } catch {
    return errorResponse("Invalid assessment request.", 400);
  }
}
