package com.uuorb.journal.util;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
@Data
public class TokenSettings {
    @Value("${jwt.secretKey:}")
    private String secretKey;

    @Value("${jwt.accessTokenExpireTime:}")
    private Duration accessTokenExpireTime;

    @Value("${jwt.refreshTokenExpireTime:}")
    private Duration refreshTokenExpireTime;

    @Value("${jwt.refreshTokenExpireAppTime:}")
    private Duration refreshTokenExpireAppTime;

    @Value("${jwt.issuer:}")
    private String issuer;

    @PostConstruct
    public void init() {
        TokenUtil.setTokenSettings(this);
    }
}
