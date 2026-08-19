package com.exacta.timer.dto.dashboard;

import java.time.Instant;
import java.util.List;

public record WeeklyHourSplit(
        Instant weekStart,
        Instant weekEnd,
        double billableHours,
        double nonBillableHours,
        double utilizationPercent,
        List<DailyHourSplit> days) {
}
