package com.exacta.timer.dto.client;

public record ClientResponse(
        Long id,
        String name,
        String contactEmail,
        String company) {
}
