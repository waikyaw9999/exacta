import type { WeeklyHourSplit } from "../../types/dashboard";
import { formatHours, formatShortDate } from "../../lib/formatters";

interface BillableHoursChartProps {
  split: WeeklyHourSplit;
}

export function BillableHoursChart({ split }: BillableHoursChartProps) {
  const totalHours = split.billableHours + split.nonBillableHours;
  const billablePercent = totalHours > 0 ? (split.billableHours / totalHours) * 100 : 0;
  const nonBillablePercent = totalHours > 0 ? 100 - billablePercent : 0;
  const maxDayHours = Math.max(
    1,
    ...split.days.map((day) => day.billableHours + day.nonBillableHours),
  );

  return (
    <section className="rounded-2xl border border-white/10 bg-slate-900/70 p-5 shadow-xl shadow-black/20">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-300">
            Billable vs non-billable
          </p>
          <h2 className="mt-2 text-lg font-semibold text-white">Current week</h2>
          <p className="mt-1 text-sm text-slate-400">
            {formatShortDate(split.weekStart)} – {formatShortDate(split.weekEnd)}
          </p>
        </div>
        <p className="rounded-full bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-300">
          {split.utilizationPercent.toFixed(0)}% billable
        </p>
      </div>

      <div className="mt-5">
        <div
          className="flex h-3 overflow-hidden rounded-full bg-slate-800"
          role="img"
          aria-label={`Billable ${formatHours(split.billableHours)}, non-billable ${formatHours(split.nonBillableHours)}`}
        >
          <div className="bg-blue-500" style={{ width: `${billablePercent}%` }} />
          <div className="bg-slate-500" style={{ width: `${nonBillablePercent}%` }} />
        </div>
        <div className="mt-3 flex flex-wrap gap-4 text-sm">
          <p className="flex items-center gap-2 text-slate-300">
            <span className="h-2.5 w-2.5 rounded-sm bg-blue-500" />
            Billable {formatHours(split.billableHours)}
          </p>
          <p className="flex items-center gap-2 text-slate-300">
            <span className="h-2.5 w-2.5 rounded-sm bg-slate-500" />
            Non-billable {formatHours(split.nonBillableHours)}
          </p>
        </div>
      </div>

      <div className="mt-6 grid grid-cols-7 gap-2">
        {split.days.map((day) => {
          const dayTotal = day.billableHours + day.nonBillableHours;
          const billableHeight = (day.billableHours / maxDayHours) * 100;
          const nonBillableHeight = (day.nonBillableHours / maxDayHours) * 100;

          return (
            <div key={day.date} className="flex min-h-36 flex-col items-center justify-end gap-2">
              <div className="flex h-28 w-full items-end justify-center gap-0.5">
                <div
                  className="w-2.5 rounded-t-sm bg-blue-500 sm:w-3"
                  style={{ height: `${Math.max(day.billableHours > 0 ? 6 : 0, billableHeight)}%` }}
                  title={`Billable ${formatHours(day.billableHours)}`}
                />
                <div
                  className="w-2.5 rounded-t-sm bg-slate-500 sm:w-3"
                  style={{ height: `${Math.max(day.nonBillableHours > 0 ? 6 : 0, nonBillableHeight)}%` }}
                  title={`Non-billable ${formatHours(day.nonBillableHours)}`}
                />
              </div>
              <p className="text-[11px] text-slate-400">{day.label}</p>
              <p className="text-[10px] tabular-nums text-slate-500">
                {dayTotal > 0 ? formatHours(dayTotal) : "—"}
              </p>
            </div>
          );
        })}
      </div>
    </section>
  );
}
