package com.exacta.timer.service;

import com.exacta.timer.dto.dashboard.DailyHourSplit;
import com.exacta.timer.dto.dashboard.DashboardResponse;
import com.exacta.timer.dto.dashboard.UnbilledRevenueSummary;
import com.exacta.timer.dto.dashboard.WeeklyHourSplit;
import com.exacta.timer.dto.timeentry.TimeEntryResponse;
import com.exacta.timer.entity.TimeEntry;
import com.exacta.timer.entity.TimeEntryStatus;
import com.exacta.timer.repository.TimeEntryRepository;
import com.exacta.timer.security.UserPrincipal;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final int RECENT_LIMIT = 8;
    private static final String[] WEEKDAY_LABELS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private final TimeEntryRepository timeEntryRepository;
    private final TimeEntryService timeEntryService;

    public DashboardResponse getDashboard(UserPrincipal actor) {
        Long userId = actor.isAdmin() ? null : actor.id();
        List<TimeEntry> entries = timeEntryRepository.search(userId, null, null);
        Instant now = Instant.now();
        return new DashboardResponse(
                calculateUnbilledRevenue(entries),
                calculateWeeklyHours(entries, now),
                recentEntries(entries));
    }

    private UnbilledRevenueSummary calculateUnbilledRevenue(List<TimeEntry> entries) {
        BigDecimal amount = BigDecimal.ZERO;
        int billableMinutes = 0;
        int entryCount = 0;

        for (TimeEntry entry : entries) {
            if (!entry.isBillable()
                    || entry.getStatus() == TimeEntryStatus.BILLED
                    || entry.getStatus() == TimeEntryStatus.RUNNING) {
                continue;
            }
            int minutes = minutesOf(entry);
            if (minutes <= 0) {
                continue;
            }
            BigDecimal hourlyRate = entry.getUser().getHourlyRate() == null
                    ? BigDecimal.ZERO
                    : entry.getUser().getHourlyRate();
            amount = amount.add(hourlyRate.multiply(BigDecimal.valueOf(minutes))
                    .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP));
            billableMinutes += minutes;
            entryCount += 1;
        }

        BigDecimal hours = BigDecimal.valueOf(billableMinutes)
                .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
        BigDecimal averageRate = hours.compareTo(BigDecimal.ZERO) > 0
                ? amount.divide(hours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new UnbilledRevenueSummary(
                amount.setScale(2, RoundingMode.HALF_UP),
                billableMinutes,
                entryCount,
                averageRate);
    }

    private WeeklyHourSplit calculateWeeklyHours(List<TimeEntry> entries, Instant now) {
        LocalDate monday = LocalDate.ofInstant(now, ZoneOffset.UTC).with(DayOfWeek.MONDAY);
        Instant weekStart = monday.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant weekEndExclusive = monday.plusDays(7).atStartOfDay().toInstant(ZoneOffset.UTC);

        List<DailyHourSplit> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            days.add(new DailyHourSplit(
                    day.toString(),
                    WEEKDAY_LABELS[i],
                    0,
                    0));
        }

        double[] billable = new double[7];
        double[] nonBillable = new double[7];

        for (TimeEntry entry : entries) {
            Instant started = entry.getStartTime();
            if (started == null || started.isBefore(weekStart) || !started.isBefore(weekEndExclusive)) {
                continue;
            }
            if (entry.getStatus() == TimeEntryStatus.RUNNING) {
                continue;
            }
            long dayIndex = java.time.Duration.between(weekStart, started).toDays();
            if (dayIndex < 0 || dayIndex > 6) {
                continue;
            }
            double hours = minutesOf(entry) / 60.0;
            if (entry.isBillable()) {
                billable[(int) dayIndex] += hours;
            } else {
                nonBillable[(int) dayIndex] += hours;
            }
        }

        List<DailyHourSplit> populated = new ArrayList<>();
        double billableHours = 0;
        double nonBillableHours = 0;
        for (int i = 0; i < 7; i++) {
            DailyHourSplit template = days.get(i);
            populated.add(new DailyHourSplit(
                    template.date(),
                    template.label(),
                    roundHours(billable[i]),
                    roundHours(nonBillable[i])));
            billableHours += billable[i];
            nonBillableHours += nonBillable[i];
        }

        double total = billableHours + nonBillableHours;
        double utilization = total > 0 ? (billableHours / total) * 100 : 0;
        Instant weekEndInclusive = weekEndExclusive.minusSeconds(1);

        return new WeeklyHourSplit(
                weekStart,
                weekEndInclusive,
                roundHours(billableHours),
                roundHours(nonBillableHours),
                roundHours(utilization),
                populated);
    }

    private List<TimeEntryResponse> recentEntries(List<TimeEntry> entries) {
        return entries.stream()
                .sorted(Comparator.comparing(TimeEntry::getStartTime).reversed())
                .limit(RECENT_LIMIT)
                .map(timeEntryService::toResponse)
                .toList();
    }

    private int minutesOf(TimeEntry entry) {
        return Math.max(0, entry.getDurationMinutes() == null ? 0 : entry.getDurationMinutes());
    }

    private double roundHours(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
