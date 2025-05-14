package com.uuorb.journal.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.uuorb.journal.model.kimi.KimiMessage;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@Component
public class KimiUtils {

    @Value("${kimi.key}")
    private String KIMI_API_KEY;

    private static final String MODELS_URL = "https://api.moonshot.cn/v1/models";

    private static final String FILES_URL = "https://api.moonshot.cn/v1/files";

    private static final String ESTIMATE_TOKEN_COUNT_URL = "https://api.moonshot.cn/v1/tokenizers/estimate-token-count";

    private static final String CHAT_COMPLETION_URL = "https://api.moonshot.cn/v1/chat/completions";

    public String getModelList() {
        return getCommonRequest(MODELS_URL).execute().body();
    }

    public String uploadFile(@NonNull File file) {
        return getCommonRequest(FILES_URL).method(Method.POST)
            .header("purpose", "file-extract")
            .form("file", file)
            .execute()
            .body();
    }

    public String getFileList() {
        return getCommonRequest(FILES_URL).execute().body();
    }

    public String deleteFile(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId).method(Method.DELETE).execute().body();
    }

    public String getFileDetail(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId).execute().body();
    }

    public String getFileContent(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId + "/content").execute().body();
    }

    public String estimateTokenCount(@NonNull String model, @NonNull List<KimiMessage> messages) {
        String requestBody = new JSONObject().putOpt("model", model).putOpt("messages", messages).toString();
        return getCommonRequest(ESTIMATE_TOKEN_COUNT_URL).method(Method.POST)
            .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
            .body(requestBody)
            .execute()
            .body();
    }

    @SneakyThrows
    public void chatInStream(@NonNull String model, @NonNull List<KimiMessage> messages, OutputStream outputStream) {
        String requestBody = new JSONObject().putOpt("model", model)
            .putOpt("messages", messages)
            .putOpt("stream", true)
            .toString();
        Request okhttpRequest = new Request.Builder().url(CHAT_COMPLETION_URL)
            .post(RequestBody.create(requestBody, MediaType.get(ContentType.JSON.getValue())))
            .addHeader("Authorization", KIMI_API_KEY)
            .build();
        Call call = new OkHttpClient().newCall(okhttpRequest);
        Response okhttpResponse = call.execute();
        BufferedReader reader = new BufferedReader(okhttpResponse.body().charStream());
        String line;
        while ((line = reader.readLine()) != null) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            if (JSONUtil.isTypeJSON(line)) {
                Optional.of(JSONUtil.parseObj(line))
                    .map(x -> x.getJSONObject("error"))
                    .map(x -> x.getStr("message"))
                    .ifPresent(x -> {
                        try {
                            outputStream.write(x.getBytes());
                            outputStream.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                return;
            }
            line = StrUtil.replace(line, "data: ", StrUtil.EMPTY);
            if (StrUtil.equals("[DONE]", line) || !JSONUtil.isTypeJSON(line)) {
                return;
            }
            Optional.of(JSONUtil.parseObj(line))
                .map(x -> x.getJSONArray("choices"))
                .filter(CollUtil::isNotEmpty)
                .map(x -> (JSONObject)x.get(0))
                .map(x -> x.getJSONObject("delta"))
                .map(x -> x.getStr("content"))
                .ifPresent(x -> {
                    try {
                        outputStream.write(x.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }

    /**
     * 相关文档：{@link <a href="https://platform.moonshot.cn/docs/api/chat#%E8%B0%83%E7%94%A8%E7%A4%BA%E4%BE%8B">点击查看文档</a> }
     *
     * @param model:    调用kimi的模型
     * @param messages: 消息列表
     * @return KimiMessage
     */
    @SneakyThrows
    public KimiMessage chat(@NonNull String model, @NonNull List<KimiMessage> messages) {
        String requestBody = new JSONObject().putOpt("model", model)
            .putOpt("messages", messages)
            .putOpt("type", "json_object")
            .putOpt("stream", false)
            .toString();

        Request okhttpRequest = new Request.Builder().url(CHAT_COMPLETION_URL)
            .post(RequestBody.create(requestBody, MediaType.get("application/json; charset=utf-8")))
            .addHeader("Authorization", KIMI_API_KEY)
            .build();

        OkHttpClient client = new OkHttpClient();
        try (Response okhttpResponse = client.newCall(okhttpRequest).execute()) {
            if (!okhttpResponse.isSuccessful()) {
                throw new IOException("Unexpected code " + okhttpResponse);
            } ;

            assert okhttpResponse.body() != null;
            String responseBody = okhttpResponse.body().string();
            JSONArray choices = new JSONObject(responseBody).getJSONArray("choices");

            if (choices == null || choices.isEmpty()) {
                throw new IllegalStateException("No choices found in response");
            }

            JSONObject choice = (JSONObject)choices.get(0);
            String messageJson = choice.getStr("message", null);
            if (messageJson == null) {
                throw new IllegalStateException("Message is missing in the choice");
            }
            return JSONUtil.toBean(messageJson, KimiMessage.class);
        }
    }

    private HttpRequest getCommonRequest(@NonNull String url) {
        return HttpRequest.of(url).header(Header.AUTHORIZATION, "Bearer " + KIMI_API_KEY);
    }

}
