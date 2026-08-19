package com.exacta.timer.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank @Size(max = 180) String name,
        @Email @Size(max = 255) String contactEmail,
        @NotBlank @Size(max = 180) String company) {
}
