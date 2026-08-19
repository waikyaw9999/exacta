import { useQuery } from "@tanstack/react-query";
import { getApiErrorMessage } from "../api/errors";
import { fetchTimeEntries } from "../api/timeTracking";
import { RecentTimeEntriesTable } from "../components/dashboard/RecentTimeEntriesTable";

export default function TimeEntriesPage() {
  const entriesQuery = useQuery({
    queryKey: ["time-entries"],
    queryFn: fetchTimeEntries,
  });

  if (entriesQuery.isLoading) {
    return <p className="text-slate-400">Loading time entries…</p>;
  }

  if (entriesQuery.isError) {
    return (
      <p className="text-rose-300">
        {getApiErrorMessage(entriesQuery.error, "Could not load time entries")}
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <header className="max-w-3xl">
        <h1 className="text-2xl font-semibold tracking-tight sm:text-3xl">Time entries</h1>
        <p className="mt-2 text-slate-400">
          Every saved log for your account. Admins see the whole firm.
        </p>
      </header>
      <RecentTimeEntriesTable entries={entriesQuery.data ?? []} />
    </div>
  );
}
