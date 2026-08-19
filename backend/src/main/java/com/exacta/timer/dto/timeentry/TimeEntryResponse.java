package com.exacta.timer.dto.timeentry;

import com.exacta.timer.entity.TimeEntryStatus;
import java.time.Instant;

public record TimeEntryResponse(
        Long id,
        Long userId,
        String userName,
        Long projectId,
        String projectName,
        Long clientId,
        String clientName,
        Instant startTime,
        Instant endTime,
        Integer durationMinutes,
        String description,
        boolean isBillable,
        TimeEntryStatus status) {
}
