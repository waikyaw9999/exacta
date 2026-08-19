package com.exacta.timer.dto.project;

import com.exacta.timer.entity.ProjectStatus;

public record ProjectResponse(
        Long id,
        String name,
        Long clientId,
        String clientName,
        ProjectStatus status) {
}
