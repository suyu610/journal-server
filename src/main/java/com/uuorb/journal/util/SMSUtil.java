package com.uuorb.journal.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
// 腾讯云发送短信
public class SMSUtil {

    @Value("${tencent.sms.secretId}")
    private String SECRET_ID;

    @Value("${tencent.sms.secretKey}")
    private String SECRET_KEY;

    public void sendLoginMsg(String phone, String code) {
        try {
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(60);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            SendSmsRequest req = getSendSmsRequest(phone, code);

            client.SendSms(req);

        } catch (TencentCloudSDKException e) {
            log.error("发送短信失败:{},phone:{}", e.getMessage(), phone);
        }
    }

    private static @NotNull SendSmsRequest getSendSmsRequest(String phone, String code) {
        SendSmsRequest req = new SendSmsRequest();
        String sdkAppId = "1400773974";
        req.setSmsSdkAppId(sdkAppId);
        String signName = "轻效科技";
        req.setSignName(signName);
        String templateId = "1625672";
        req.setTemplateId(templateId);

        String[] templateParamSet = {code};
        req.setTemplateParamSet(templateParamSet);

        String[] phoneNumberSet = {"+86" + phone};
        req.setPhoneNumberSet(phoneNumberSet);

        String sessionContext = "";
        req.setSessionContext(sessionContext);

        String extendCode = "";
        req.setExtendCode(extendCode);

        String senderid = "";
        req.setSenderId(senderid);
        return req;
    }

}
