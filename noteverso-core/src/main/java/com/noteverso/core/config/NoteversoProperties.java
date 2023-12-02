package com.noteverso.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "noteverso")
@Data
@Component
public class NoteversoProperties {
    private String jwtSecret;

    private int jwtExpirationDays;
}
