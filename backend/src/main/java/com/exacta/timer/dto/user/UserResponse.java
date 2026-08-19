package com.exacta.timer.dto.user;

import com.exacta.timer.entity.Role;
import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        BigDecimal hourlyRate) {
}
