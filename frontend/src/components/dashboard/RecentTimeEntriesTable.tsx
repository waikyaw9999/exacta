import type { TimeEntry } from "../../types/api";
import { formatDateTime, formatDurationMinutes } from "../../lib/formatters";

interface RecentTimeEntriesTableProps {
  entries: TimeEntry[];
}

function billableLabel(entry: TimeEntry): string {
  if (!entry.isBillable) {
    return "Non-billable";
  }
  if (entry.status === "BILLED") {
    return "Billed";
  }
  if (entry.status === "SUBMITTED") {
    return "Ready to bill";
  }
  return "Unbilled";
}

function billableClass(entry: TimeEntry): string {
  if (!entry.isBillable) {
    return "bg-slate-800 text-slate-300";
  }
  if (entry.status === "BILLED") {
    return "bg-emerald-500/15 text-emerald-300";
  }
  return "bg-amber-500/15 text-amber-200";
}

export function RecentTimeEntriesTable({ entries }: RecentTimeEntriesTableProps) {
  return (
    <section className="overflow-hidden rounded-2xl border border-white/10 bg-slate-900/70 shadow-xl shadow-black/20">
      <div className="border-b border-white/10 px-5 py-4">
        <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-300">
          Recent time entries
        </p>
        <h2 className="mt-2 text-lg font-semibold text-white">Latest time logs</h2>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-950/60 text-[11px] uppercase tracking-wider text-slate-400">
            <tr>
              <th className="px-5 py-3 font-medium">When</th>
              <th className="px-5 py-3 font-medium">Client</th>
              <th className="px-5 py-3 font-medium">Project</th>
              <th className="px-5 py-3 font-medium">Duration</th>
              <th className="px-5 py-3 font-medium">Billable status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            {entries.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-5 py-8 text-center text-slate-400">
                  No time entries yet. Start the timer to capture billable work.
                </td>
              </tr>
            ) : (
              entries.map((entry) => (
              <tr key={entry.id} className="hover:bg-white/[0.03]">
                <td className="whitespace-nowrap px-5 py-3 text-slate-400">
                  {formatDateTime(entry.startTime)}
                </td>
                <td className="px-5 py-3 font-medium text-slate-100">{entry.clientName}</td>
                <td className="px-5 py-3 text-slate-300">
                  <p>{entry.projectName}</p>
                  {entry.description ? (
                    <p className="mt-0.5 max-w-xs truncate text-xs text-slate-500">
                      {entry.description}
                    </p>
                  ) : null}
                </td>
                <td className="whitespace-nowrap px-5 py-3 tabular-nums text-slate-200">
                  {formatDurationMinutes(entry.durationMinutes ?? 0)}
                </td>
                <td className="px-5 py-3">
                  <span
                    className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${billableClass(entry)}`}
                  >
                    {billableLabel(entry)}
                  </span>
                </td>
              </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
