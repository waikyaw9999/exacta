export function LandingProductVisual() {
  return (
    <div
      className="landing-visual relative mx-auto w-full max-w-xl"
      aria-hidden="true"
    >
      <div className="absolute inset-0 translate-y-6 rounded-[2rem] bg-[#0c2f2a]/10 blur-2xl" />
      <div className="relative overflow-hidden rounded-[1.75rem] border border-[#1a3d38]/15 bg-[#0f1f1c] text-[#e8f0ee] shadow-[0_24px_60px_-28px_rgba(12,47,42,0.55)]">
        <div className="flex items-center justify-between border-b border-white/10 px-5 py-3">
          <div>
            <p className="font-landing-display text-lg tracking-tight text-white">Exacta</p>
            <p className="text-xs text-white/50">Live firm snapshot</p>
          </div>
          <p className="text-xs tabular-nums text-[#8fd6c4]">$18,420 unbilled</p>
        </div>

        <div className="grid gap-3 p-5 sm:grid-cols-[1.1fr_0.9fr]">
          <div className="space-y-3">
            <div className="rounded-xl bg-white/[0.04] px-4 py-3">
              <p className="text-[11px] uppercase tracking-[0.16em] text-white/40">This week</p>
              <div className="mt-3 flex h-2 overflow-hidden rounded-full bg-white/10">
                <div className="w-[78%] bg-[#2f9e86]" />
                <div className="w-[22%] bg-white/25" />
              </div>
              <p className="mt-2 text-sm text-white/70">
                31.2h billable · 8.4h non-billable
              </p>
            </div>
            <div className="rounded-xl bg-white/[0.04] px-4 py-3">
              <p className="text-[11px] uppercase tracking-[0.16em] text-white/40">
                Recent entry
              </p>
              <p className="mt-2 text-sm text-white">Northwind Legal · Merger diligence</p>
              <p className="mt-1 text-xs text-white/50">2h 30m · ready to bill</p>
            </div>
          </div>

          <div className="landing-timer flex flex-col justify-between rounded-xl border border-[#2f9e86]/35 bg-[#102824] p-4">
            <div>
              <p className="text-[11px] font-medium uppercase tracking-[0.18em] text-[#8fd6c4]">
                Tracking
              </p>
              <p className="mt-2 font-mono text-3xl tabular-nums tracking-tight text-white">
                01:24:07
              </p>
              <p className="mt-3 text-sm text-white/70">Harbor Partners</p>
              <p className="text-xs text-white/45">Litigation support — Chen</p>
            </div>
            <div className="mt-6 h-10 rounded-lg bg-[#c45c4a] text-center text-sm font-semibold leading-10 text-white">
              Stop
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
