package com.roastmyresume.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.gemini")
@Getter
@Setter
public class AppConfig {
    private String apiKey;
    private String apiUrl;
    private int maxTokens;
}