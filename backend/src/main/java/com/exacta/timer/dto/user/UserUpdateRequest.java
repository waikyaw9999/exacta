package com.exacta.timer.dto.user;

import com.exacta.timer.entity.Role;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UserUpdateRequest(
        @Size(max = 120) String name,
        @Email @Size(max = 255) String email,
        @Size(min = 8, max = 72) String password,
        Role role,
        @DecimalMin("0.00") BigDecimal hourlyRate) {
}
