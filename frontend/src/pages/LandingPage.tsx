import { Link } from "react-router-dom";
import { LandingProductVisual } from "../components/landing/LandingProductVisual";
import { useAuthStore } from "../stores/authStore";

const improvements = [
  {
    title: "Capture time before it disappears",
    body: "A persistent timer stays with you between matters, so hallway advice and quick reviews still become billable work.",
  },
  {
    title: "See unbilled revenue clearly",
    body: "The dashboard turns hours into dollars using each attorney’s rate, so partners know what is still waiting to invoice.",
  },
  {
    title: "Protect utilization without busywork",
    body: "Billable versus non-billable hours for the week make coaching conversations concrete instead of anecdotal.",
  },
] as const;

export default function LandingPage() {
  const accessToken = useAuthStore((state) => state.accessToken);
  const primaryTo = accessToken ? "/app" : "/login";
  const primaryLabel = accessToken ? "Open dashboard" : "Sign in to try the demo";

  return (
    <div className="landing-page min-h-screen text-[#14201d]">
      <header className="landing-fade mx-auto flex max-w-6xl items-center justify-between px-4 py-5 sm:px-6">
        <p className="font-landing-display text-2xl tracking-tight text-[#0c2f2a] sm:text-3xl">
          Exacta
        </p>
        <div className="flex items-center gap-2 sm:gap-3">
          {!accessToken ? (
            <Link
              to="/register"
              className="hidden rounded-lg px-3 py-2 text-sm font-medium text-[#1f3d37] transition hover:bg-[#0c2f2a]/5 sm:inline-block"
            >
              Create account
            </Link>
          ) : null}
          <Link
            to={primaryTo}
            className="rounded-lg bg-[#0c2f2a] px-4 py-2 text-sm font-semibold text-[#f3f7f5] transition hover:bg-[#164038]"
          >
            {accessToken ? "Dashboard" : "Sign in"}
          </Link>
        </div>
      </header>

      <main>
        <section className="relative overflow-hidden">
          <div className="pointer-events-none absolute inset-0 landing-hero-atmosphere" />
          <div className="relative mx-auto grid max-w-6xl gap-12 px-4 pb-16 pt-8 sm:px-6 lg:grid-cols-[1.05fr_0.95fr] lg:items-center lg:pb-24 lg:pt-10">
            <div className="landing-fade landing-fade-delay-1 max-w-xl">
              <h1 className="font-landing-display text-[2.6rem] leading-[1.05] tracking-tight text-[#0c2f2a] sm:text-5xl lg:text-[3.4rem]">
                Exacta
              </h1>
              <p className="mt-5 font-landing-display text-2xl leading-snug tracking-tight text-[#1f3d37] sm:text-3xl">
                Stop losing billable hours between meetings.
              </p>
              <p className="mt-4 max-w-md text-base leading-relaxed text-[#3d524c] sm:text-lg">
                Time tracking and profitability for law firms and consultants —
                built to make capture effortless and unbilled work impossible to ignore.
              </p>
              <div className="mt-8 flex flex-wrap items-center gap-3">
                <Link
                  to={primaryTo}
                  className="rounded-lg bg-[#c45c4a] px-5 py-3 text-sm font-semibold text-white transition hover:bg-[#b14f3e]"
                >
                  {primaryLabel}
                </Link>
                <a
                  href="#how-it-helps"
                  className="rounded-lg px-5 py-3 text-sm font-semibold text-[#0c2f2a] transition hover:bg-[#0c2f2a]/5"
                >
                  See how it helps
                </a>
              </div>
            </div>

            <div className="landing-fade landing-fade-delay-2">
              <LandingProductVisual />
            </div>
          </div>
        </section>

        <section className="border-t border-[#0c2f2a]/10 bg-[#f3f7f5]/70">
          <div className="mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-20">
            <h2 className="font-landing-display max-w-2xl text-3xl tracking-tight text-[#0c2f2a] sm:text-4xl">
              Every unlogged hour is revenue you already earned.
            </h2>
            <p className="mt-4 max-w-2xl text-base leading-relaxed text-[#3d524c] sm:text-lg">
              Associates finish a call and move on. Partners ask for status and get
              memory instead of numbers. Exacta exists so the firm can see the work
              while it is still fresh — and still billable.
            </p>
          </div>
        </section>

        <section id="how-it-helps" className="scroll-mt-8">
          <div className="mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-20">
            <h2 className="font-landing-display text-3xl tracking-tight text-[#0c2f2a] sm:text-4xl">
              How Exacta improves daily practice
            </h2>
            <p className="mt-3 max-w-2xl text-base text-[#3d524c] sm:text-lg">
              Less admin friction for fee earners. Clearer foresight for partners.
            </p>
            <div className="mt-12 grid gap-10 md:grid-cols-3 md:gap-8">
              {improvements.map((item, index) => (
                <article key={item.title} className="landing-fade" style={{ animationDelay: `${0.12 * (index + 1)}s` }}>
                  <p className="font-mono text-xs tabular-nums text-[#2f9e86]">
                    0{index + 1}
                  </p>
                  <h3 className="mt-3 font-landing-display text-xl tracking-tight text-[#0c2f2a]">
                    {item.title}
                  </h3>
                  <p className="mt-3 text-sm leading-relaxed text-[#3d524c] sm:text-base">
                    {item.body}
                  </p>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section className="border-t border-[#0c2f2a]/10 bg-[#0c2f2a] text-[#f3f7f5]">
          <div className="mx-auto flex max-w-6xl flex-col gap-6 px-4 py-16 sm:flex-row sm:items-end sm:justify-between sm:px-6 sm:py-20">
            <div className="max-w-xl">
              <h2 className="font-landing-display text-3xl tracking-tight sm:text-4xl">
                Built for firms that bill by the hour.
              </h2>
              <p className="mt-4 text-base leading-relaxed text-[#b7cbc4]">
                Try the live demo with seeded clients, projects, and a week of time
                entries. Start the floating timer, stop it once, and watch unbilled
                revenue update.
              </p>
            </div>
            <Link
              to={primaryTo}
              className="inline-flex shrink-0 rounded-lg bg-[#f3f7f5] px-5 py-3 text-sm font-semibold text-[#0c2f2a] transition hover:bg-white"
            >
              {primaryLabel}
            </Link>
          </div>
        </section>
      </main>

      <footer className="border-t border-[#0c2f2a]/10">
        <div className="mx-auto flex max-w-6xl flex-col gap-2 px-4 py-8 text-sm text-[#5a6f69] sm:flex-row sm:items-center sm:justify-between sm:px-6">
          <p className="font-landing-display text-lg text-[#0c2f2a]">Exacta</p>
          <p>Professional time tracking and billing.</p>
        </div>
      </footer>
    </div>
  );
}
