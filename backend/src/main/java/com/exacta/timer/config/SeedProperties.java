package com.exacta.timer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.seed")
public class SeedProperties {

    private boolean enabled = true;
    private String adminEmail = "ada@exacta.test";
    private String adminPassword = "ExactaDemo1!";
    private String memberPassword = "ExactaDemo1!";
}
