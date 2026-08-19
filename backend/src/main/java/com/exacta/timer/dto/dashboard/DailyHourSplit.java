package com.exacta.timer.dto.dashboard;

public record DailyHourSplit(
        String date,
        String label,
        double billableHours,
        double nonBillableHours) {
}
