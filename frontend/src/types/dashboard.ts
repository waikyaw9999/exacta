import type { TimeEntry } from "./api";

export interface UnbilledRevenueSummary {
  amount: number;
  billableMinutes: number;
  entryCount: number;
  averageHourlyRate: number;
}

export interface DailyHourSplit {
  date: string;
  label: string;
  billableHours: number;
  nonBillableHours: number;
}

export interface WeeklyHourSplit {
  weekStart: string;
  weekEnd: string;
  billableHours: number;
  nonBillableHours: number;
  utilizationPercent: number;
  days: DailyHourSplit[];
}

export interface DashboardResponse {
  unbilledRevenue: UnbilledRevenueSummary;
  weeklyHours: WeeklyHourSplit;
  recentEntries: TimeEntry[];
}
