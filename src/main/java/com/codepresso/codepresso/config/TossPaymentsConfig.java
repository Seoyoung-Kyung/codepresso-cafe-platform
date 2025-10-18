package com.codepresso.codepresso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "toss.payments")
@Component
@Data
public class TossPaymentsConfig {
    private String clientKey;
    private String secretKey;
    private String baseUrl;
    private String successUrl;
    private String failureUrl;
}
