package com.exacta.timer.dto.dashboard;

import java.math.BigDecimal;

public record UnbilledRevenueSummary(
        BigDecimal amount,
        int billableMinutes,
        int entryCount,
        BigDecimal averageHourlyRate) {
}
