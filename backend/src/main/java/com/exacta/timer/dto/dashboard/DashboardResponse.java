package com.exacta.timer.dto.dashboard;

import com.exacta.timer.dto.timeentry.TimeEntryResponse;
import java.util.List;

public record DashboardResponse(
        UnbilledRevenueSummary unbilledRevenue,
        WeeklyHourSplit weeklyHours,
        List<TimeEntryResponse> recentEntries) {
}
