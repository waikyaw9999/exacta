package com.exacta.timer.dto.timeentry;

import com.exacta.timer.entity.TimeEntryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StartTimerRequest(
        @NotNull Long projectId,
        @Size(max = 1000) String description,
        Boolean isBillable) {
}
