package com.uuorb.journal.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudConfig {
    private String secretId;
    private String secretKey;
    private String bucket;
    private String region;
    private String allowPrefix;


    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setAllowPrefix(String allowPrefix) {
        this.allowPrefix = allowPrefix;
    }
}
