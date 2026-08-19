import type { UnbilledRevenueSummary } from "../../types/dashboard";
import { formatCurrency, formatDurationMinutes } from "../../lib/formatters";

interface UnbilledRevenueCardProps {
  summary: UnbilledRevenueSummary;
}

export function UnbilledRevenueCard({ summary }: UnbilledRevenueCardProps) {
  return (
    <section className="rounded-2xl border border-white/10 bg-slate-900/70 p-5 shadow-xl shadow-black/20">
      <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-300">
        Unbilled revenue
      </p>
      <p className="mt-3 text-3xl font-semibold tracking-tight text-white sm:text-4xl">
        {formatCurrency(summary.amount)}
      </p>
      <p className="mt-2 text-sm text-slate-400">
        Billable time that has not been invoiced yet.
      </p>
      <dl className="mt-5 grid grid-cols-2 gap-3 text-sm">
        <div className="rounded-xl bg-slate-950/70 px-3 py-2">
          <dt className="text-slate-400">Unbilled time</dt>
          <dd className="mt-1 font-medium text-slate-100">
            {formatDurationMinutes(summary.billableMinutes)}
          </dd>
        </div>
        <div className="rounded-xl bg-slate-950/70 px-3 py-2">
          <dt className="text-slate-400">Open entries</dt>
          <dd className="mt-1 font-medium text-slate-100">{summary.entryCount}</dd>
        </div>
      </dl>
      <p className="mt-4 text-xs text-slate-500">
        Blended rate {formatCurrency(summary.averageHourlyRate)}/hr
      </p>
    </section>
  );
}
