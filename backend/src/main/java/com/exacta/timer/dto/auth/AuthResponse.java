package com.exacta.timer.dto.auth;

import com.exacta.timer.dto.user.UserResponse;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user) {

    public static AuthResponse bearer(String accessToken, UserResponse user) {
        return new AuthResponse(accessToken, "Bearer", user);
    }
}
