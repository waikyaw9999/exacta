import { useQuery } from "@tanstack/react-query";
import { fetchDashboard } from "../api/dashboard";
import { getApiErrorMessage } from "../api/errors";
import { BillableHoursChart } from "../components/dashboard/BillableHoursChart";
import { RecentTimeEntriesTable } from "../components/dashboard/RecentTimeEntriesTable";
import { UnbilledRevenueCard } from "../components/dashboard/UnbilledRevenueCard";

export default function DashboardPage() {
  const dashboardQuery = useQuery({
    queryKey: ["dashboard"],
    queryFn: fetchDashboard,
  });

  if (dashboardQuery.isLoading) {
    return <p className="text-slate-400">Loading profitability analytics…</p>;
  }

  if (dashboardQuery.isError || !dashboardQuery.data) {
    return (
      <p className="text-rose-300">
        {getApiErrorMessage(dashboardQuery.error, "Could not load the dashboard")}
      </p>
    );
  }

  const { unbilledRevenue, weeklyHours, recentEntries } = dashboardQuery.data;

  return (
    <div className="space-y-6">
      <header className="max-w-3xl">
        <h1 className="text-2xl font-semibold tracking-tight sm:text-3xl">Dashboard</h1>
        <p className="mt-2 text-slate-400">
          Live unbilled revenue, utilization this week, and the latest time logs.
          Stop the timer to save a billable entry and refresh these numbers.
        </p>
      </header>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-5">
        <div className="lg:col-span-2">
          <UnbilledRevenueCard summary={unbilledRevenue} />
        </div>
        <div className="lg:col-span-3">
          <BillableHoursChart split={weeklyHours} />
        </div>
      </div>

      <RecentTimeEntriesTable entries={recentEntries} />
    </div>
  );
}
