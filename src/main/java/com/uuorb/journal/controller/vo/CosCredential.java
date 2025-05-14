package com.uuorb.journal.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CosCredential {
    private String secretId;
    private String secretKey;
    private String sessionToken;
    private long startTime;
    private long expiredTime;
}
