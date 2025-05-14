package com.uuorb.journal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "tencent.wechat.open")
public class WechatOpenConfig {
    private String secret;
    private String appid;
}
