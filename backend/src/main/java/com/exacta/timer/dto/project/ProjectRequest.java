package com.exacta.timer.dto.project;

import com.exacta.timer.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank @Size(max = 180) String name,
        @NotNull Long clientId,
        @NotNull ProjectStatus status) {
}
