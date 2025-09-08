package com.noteverso.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "oss")
@Data
@Component
public class OssClientProperties {
    private String endpoint;

    private String accessKey;

    private String accessKeySecret;

    private String bucketName;

    private String isHttps;
}
