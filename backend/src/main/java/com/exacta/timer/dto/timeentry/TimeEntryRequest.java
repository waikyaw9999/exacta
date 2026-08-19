package com.exacta.timer.dto.timeentry;

import com.exacta.timer.entity.TimeEntryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record TimeEntryRequest(
        Long userId,
        @NotNull Long projectId,
        @NotNull Instant startTime,
        Instant endTime,
        @Size(max = 1000) String description,
        @NotNull Boolean isBillable,
        TimeEntryStatus status) {
}
