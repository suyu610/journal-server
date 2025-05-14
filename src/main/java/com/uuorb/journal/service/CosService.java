package com.uuorb.journal.service;

import com.uuorb.journal.config.TencentCloudConfig;
import com.uuorb.journal.controller.vo.CosCredential;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;

import java.io.IOException;
import java.util.TreeMap;

@Service
public class CosService {
    @Resource
    private TencentCloudConfig tencentCloudConfig;

    public CosCredential getCredential() {

        TreeMap<String, Object> config = new TreeMap<>();
        config.put("secretId", tencentCloudConfig.getSecretId());
        config.put("secretKey", tencentCloudConfig.getSecretKey());

        config.put("durationSeconds", 180);
        config.put("bucket", tencentCloudConfig.getBucket());
        config.put("region", tencentCloudConfig.getRegion());

        config.put("allowPrefixes", new String[] {tencentCloudConfig.getAllowPrefix()});

        config.put("allowActions",
                new String[] {"name/cos:PutObject", "name/cos:PostObject",
                        "name/cos:InitiateMultipartUpload", "name/cos:ListMultipartUploads",
                        "name/cos:ListParts", "name/cos:UploadPart", "name/cos:CompleteMultipartUpload"});

        try {
            Response response = CosStsClient.getCredential(config);
            return new CosCredential().setSecretId(response.credentials.tmpSecretId)
                    .setSecretKey(response.credentials.tmpSecretKey)
                    .setSessionToken(response.credentials.sessionToken)
                    .setStartTime(response.startTime).setExpiredTime(response.expiredTime);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
