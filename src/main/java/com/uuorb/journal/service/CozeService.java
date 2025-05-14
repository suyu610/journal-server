package com.uuorb.journal.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.uuorb.journal.model.AIConfig;
import com.uuorb.journal.model.EngelExpense;
import com.uuorb.journal.model.Expense;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.poi.util.NotImplemented;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.firstNonEmpty;

@Service("coze")
@Slf4j
public class CozeService implements AiService {

    @Value("${coze.token}")
    private String TOKEN;

    private static String GENERATE_IMAGE_WORKFLOW_ID = "7425232407248322598";

    private static String TTS_WORKFLOW_ID = "7427813081563414554";

    private static String EXPENSE_FORMAT_WORKFLOW_ID = "7427869736202944549";

    private static final String WORKFLOW_URL = "https://api.coze.cn/v1/workflow/run";

    @Override
    public Expense formatExpense(String naturalSentence) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject req = new JSONObject();
        JSONObject params = new JSONObject();
        params.set("text", naturalSentence);
        req.set("workflow_id", EXPENSE_FORMAT_WORKFLOW_ID);
        req.set("parameters", params);

        RequestBody body = RequestBody.create(req.toJSONString(0), JSON);
        Request request = new Request.Builder().url(WORKFLOW_URL)
            .post(body)
            .addHeader("Authorization", TOKEN)
            .addHeader("Content-Type", "application/json")
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                JSONObject data = new JSONObject(responseData);
                JSONObject jsonObject = data.getJSONObject("data");// new JSONObject(responseData);

                String output = jsonObject.getStr("output");

                try {
                    return com.alibaba.fastjson2.JSONObject.parseObject(output, Expense.class);
                } catch (Exception e) {
                    return null;
                }
            } else {
                log.error("formatExpense,请求失败:{},{}", response.code(), naturalSentence);
                //                System.out.println("请求失败: " + response.code());
                return null;
            }
        } catch (IOException e) {
            return null;
        }

    }

    @Override
    public String praise(String sentence) {
        return "";
    }

    @Override
    public String praise(String sentence, AIConfig config) {
        return "";
    }

    @Override
    public String greet(AIConfig aiConfig) {
        return "";
    }

    @Override
    public String generateImage(String model, String description, String role) {
        String result = workflowGenerateImage(role, description, model);

        return result;
    }

    @Override
    public void praiseStream(String sentence, AIConfig config, OutputStream outputStream) {

    }

    @Override
    public String tts(String sentence, AIConfig aiConfig) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject req = getEntries(sentence, aiConfig);

        RequestBody body = RequestBody.create(req.toJSONString(0), JSON);
        Request request = new Request.Builder().url(WORKFLOW_URL)
            .post(body)
            .addHeader("Authorization", TOKEN)
            .addHeader("Content-Type", "application/json")
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseData = response.body().string();
                JSONObject data = new JSONObject(responseData);
                JSONObject jsonObject = data.getJSONObject("data");// new JSONObject(responseData);
                JSONObject data1 = jsonObject.getJSONObject("data1");
                JSONObject data2 = jsonObject.getJSONObject("data2");
                JSONObject data3 = jsonObject.getJSONObject("data3");
                JSONObject data4 = jsonObject.getJSONObject("data4");

                JSONObject existObj = firstNotNullJsonObj(data1, data2, data3, data4);
                if (existObj != null) {
                    return existObj.getStr("url");
                }
            } else {
                System.out.println("请求失败: " + response.code());
            }
            return "请求失败";
        } catch (IOException e) {
            return "请求失败";
        }
    }

    @NotImplemented
    @Override
    public void engelExplain(List<EngelExpense> expenseList, OutputStream outputStream) {
        log.error("NOT IMPLEMENTED");
    }

    private static @NotNull JSONObject getEntries(String sentence, AIConfig aiConfig) {
        JSONObject req = new JSONObject();
        req.set("workflow_id", TTS_WORKFLOW_ID);

        JSONObject params = new JSONObject();

        String relationship = aiConfig.getRelationship();
        if (StrUtil.equals(relationship, "女朋友")) {
            params.set("model", "yujie");
        }

        if (StrUtil.equals(relationship, "女儿")) {
            params.set("model", "nvsheng");
        }

        if (StrUtil.equals(relationship, "儿子")) {
            params.set("model", "qingnian");
        }

        if (StrUtil.equals(relationship, "男朋友")) {
            params.set("model", "ahu");
        }

        params.set("text", sentence);
        req.set("parameters", params);
        return req;
    }

    JSONObject firstNotNullJsonObj(JSONObject... arrs) {
        return ArrayUtil.firstMatch(Objects::nonNull, arrs);
    }

    public String workflowGenerateImage(String relationship, String description, String model) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject req = new JSONObject();
        JSONObject params = new JSONObject();
        params.set("relationship", relationship);
        params.set("description", description);
        params.set("model", model);
        req.set("workflow_id", GENERATE_IMAGE_WORKFLOW_ID);
        req.set("parameters", params);

        RequestBody body = RequestBody.create(req.toJSONString(0), JSON);
        Request request = new Request.Builder().url(WORKFLOW_URL)
            .post(body)
            .addHeader("Authorization", TOKEN)
            .addHeader("Content-Type", "application/json")
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                System.out.println(responseData);
                JSONObject data = new JSONObject(responseData);
                JSONObject jsonObject = data.getJSONObject("data");// new JSONObject(responseData);

                String output1 = jsonObject.getStr("output1");
                String output2 = jsonObject.getStr("output2");
                String msg1 = jsonObject.getStr("msg1");
                String msg2 = jsonObject.getStr("msg2");

                return firstNonEmpty(output1, output2, msg1, msg2);
            } else {
                System.out.println("请求失败: " + response.code());
                return "请求失败";
            }
        } catch (IOException e) {
            return "请求失败";
        }
    }

}
