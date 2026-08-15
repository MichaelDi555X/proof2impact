"use client";

import { FormEvent, useMemo, useState } from "react";

type AiResult = { summary: string; readiness: number; gaps: string[]; nextSteps: string[] };

const docs = [
  "Organisation registration / legal identity",
  "Project or programme description",
  "Budget or funding request",
  "Evidence of impact / beneficiaries",
  "Contact and verification details",
];

const pathways = [
  ["Community foundations", "High", "Local, measurable community outcomes"],
  ["Corporate giving programmes", "High", "Clear social-impact and reporting potential"],
  ["NGO / institutional funders", "Medium", "Stronger fit after evidence and budget review"],
];

export default function Home() {
  const [project, setProject] = useState("");
  const [location, setLocation] = useState("Kenya");
  const [impact, setImpact] = useState("");
  const [selected, setSelected] = useState<string[]>([]);
  const [result, setResult] = useState<AiResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const readiness = useMemo(() => Math.round((selected.length / docs.length) * 100), [selected]);

  const toggle = (doc: string) => setSelected((x) => x.includes(doc) ? x.filter((d) => d !== doc) : [...x, doc]);

  async function assess(e: FormEvent) {
    e.preventDefault(); setLoading(true); setError(""); setResult(null);
    try {
      const r = await fetch("/api/ai/assist", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ project, location, impact, selectedDocs: selected }) });
      const data = await r.json();
      if (!r.ok) throw new Error(data.error || "AI assistant unavailable");
      setResult(data);
    } catch (err) { setError(err instanceof Error ? err.message : "AI assistant unavailable"); }
    finally { setLoading(false); }
  }

  return (
    <main className="min-h-screen bg-slate-950 text-slate-100">
      <header className="border-b border-white/10 bg-slate-950/90 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-5 py-4 lg:px-8">
          <div><p className="text-lg font-bold">Proof2Impact</p><p className="text-xs text-slate-400">Evidence → verification → impact connections</p></div>
          <span className="rounded-full border border-emerald-400/30 bg-emerald-400/10 px-3 py-1 text-xs text-emerald-300">Production</span>
        </div>
      </header>

      <section className="mx-auto grid max-w-7xl gap-8 px-5 py-12 lg:grid-cols-[1.25fr_.75fr] lg:px-8 lg:py-16">
        <div>
          <span className="inline-flex rounded-full border border-cyan-400/20 bg-cyan-400/10 px-3 py-1 text-xs text-cyan-200">AI-assisted impact verification</span>
          <h1 className="mt-5 max-w-4xl text-4xl font-semibold tracking-tight sm:text-6xl">Turn credible evidence into the right funding conversation.</h1>
          <p className="mt-5 max-w-3xl text-lg leading-8 text-slate-300">Assemble evidence, identify documentation gaps, prepare an impact brief and improve alignment with organisations, donors and companies.</p>
          <div className="mt-7 grid gap-3 sm:grid-cols-3">{["Evidence readiness", "Impact narrative", "Funder alignment"].map((x, i) => <div key={x} className="rounded-2xl border border-white/10 bg-white/[.04] p-4"><p className="text-sm">0{i + 1}</p><p className="mt-2 text-sm text-slate-300">{x}</p></div>)}</div>
        </div>
        <aside className="rounded-3xl border border-white/10 bg-white/[.05] p-6 shadow-2xl shadow-cyan-950/20">
          <p className="text-sm font-medium text-cyan-200">Documentation readiness</p>
          <div className="mt-4 flex items-end justify-between"><span className="text-4xl font-semibold">{readiness}%</span><span className="text-sm text-slate-400">{selected.length}/{docs.length}</span></div>
          <div className="mt-4 h-2 overflow-hidden rounded-full bg-white/10"><div className="h-full rounded-full bg-cyan-400 transition-all" style={{ width: `${readiness}%` }} /></div>
          <p className="mt-4 text-xs leading-5 text-slate-400">Completeness only. This does not certify authenticity, legal status, eligibility or funding approval.</p>
        </aside>
      </section>

      <section className="mx-auto grid max-w-7xl gap-6 px-5 pb-16 lg:grid-cols-[1.1fr_.9fr] lg:px-8">
        <form onSubmit={assess} className="rounded-3xl border border-white/10 bg-white/[.04] p-6 lg:p-8">
          <div className="flex items-center justify-between gap-4"><div><h2 className="text-xl font-semibold">Impact assessment</h2><p className="mt-1 text-sm text-slate-400">Share only information you are authorised to share.</p></div><span className="rounded-full bg-white/10 px-3 py-1 text-xs">Fast AI</span></div>
          <div className="mt-7 grid gap-5 sm:grid-cols-2">
            <label className="sm:col-span-2"><span className="label">Organisation / project</span><input className="field" value={project} onChange={(e) => setProject(e.target.value)} placeholder="e.g. Community water access project" required /></label>
            <label><span className="label">Country / region</span><input className="field" value={location} onChange={(e) => setLocation(e.target.value)} /></label>
            <label><span className="label">Primary impact area</span><input className="field" value={impact} onChange={(e) => setImpact(e.target.value)} placeholder="Education, health, livelihoods..." required /></label>
          </div>
          <div className="mt-7"><p className="label">Evidence currently available</p><div className="mt-3 grid gap-2 sm:grid-cols-2">{docs.map((doc) => <button type="button" key={doc} onClick={() => toggle(doc)} className={`rounded-xl border p-3 text-left text-sm transition ${selected.includes(doc) ? "border-cyan-400/60 bg-cyan-400/10 text-cyan-100" : "border-white/10 bg-black/10 text-slate-300 hover:border-white/20"}`}>{selected.includes(doc) ? "✓" : "○"} <span className="ml-2">{doc}</span></button>)}</div></div>
          <button disabled={loading} className="mt-7 w-full rounded-xl bg-cyan-400 px-5 py-3 font-semibold text-slate-950 hover:bg-cyan-300 disabled:opacity-60">{loading ? "Analysing…" : "Run AI impact assessment"}</button>
          {error && <p className="mt-4 rounded-xl border border-amber-400/30 bg-amber-400/10 p-3 text-sm text-amber-200">{error}</p>}
        </form>

        <div className="space-y-6">
          <section className="rounded-3xl border border-white/10 bg-white/[.04] p-6"><h2 className="text-xl font-semibold">AI assessment</h2>{result ? <div className="mt-5 space-y-5"><div className="rounded-2xl bg-black/20 p-4"><p className="text-sm leading-6 text-slate-300">{result.summary}</p><p className="mt-3 text-2xl font-semibold">{result.readiness}% readiness</p></div><div><p className="text-sm font-medium">Evidence gaps</p><ul className="mt-2 space-y-2 text-sm text-slate-300">{result.gaps.map((x) => <li key={x}>• {x}</li>)}</ul></div><div><p className="text-sm font-medium">Recommended next steps</p><ol className="mt-2 space-y-2 text-sm text-slate-300">{result.nextSteps.map((x, i) => <li key={x}>{i + 1}. {x}</li>)}</ol></div></div> : <p className="mt-4 text-sm leading-6 text-slate-400">The assistant will convert your evidence checklist into a concise, decision-ready action plan.</p>}</section>
          <section className="rounded-3xl border border-white/10 bg-white/[.04] p-6"><div className="flex items-center justify-between"><h2 className="text-xl font-semibold">Potential pathways</h2><span className="text-xs text-slate-500">Initial matching</span></div><div className="mt-4 space-y-3">{pathways.map(([name, fit, reason]) => <div key={name} className="rounded-2xl border border-white/10 p-4"><div className="flex justify-between gap-3"><p className="font-medium">{name}</p><span className="text-xs text-emerald-300">{fit} fit</span></div><p className="mt-1 text-sm text-slate-400">{reason}</p></div>)}</div><p className="mt-4 text-xs leading-5 text-slate-500">Decision support only — not a guarantee of funding, endorsement or eligibility.</p></section>
        </div>
      </section>
      <footer className="border-t border-white/10 px-5 py-8 text-center text-xs text-slate-500">Proof2Impact • Verify before you claim • Connect impact with integrity</footer>
    </main>
  );
}
