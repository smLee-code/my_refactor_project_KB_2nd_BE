package org.funding.openAi.client;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OpenAIClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    @Value("${openai.api.key}")
    private String apiKey;

    public String askOpenAI(String prompt) {
        OkHttpClient client = new OkHttpClient();

        try {
            // 1. 요청 바디 구성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);
            requestBody.put("messages", messages);

            // 2. 요청 생성
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), MEDIA_TYPE))
                    .build();

            // 3. 요청 실행
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("OpenAI API 호출 실패: HTTP {}", response.code());
                return "OpenAI API 요청 실패 (" + response.code() + ")";
            }

            String responseBody = response.body().string();
            log.info("OpenAI 응답: {}", responseBody);

            // 4. 응답 파싱
            JSONObject json = new JSONObject(responseBody);
            if (!json.has("choices")) {
                log.error("OpenAI 응답에 'choices' 키가 없음: {}", responseBody);
                return "OpenAI 응답 오류: 예상한 응답 형식이 아님.";
            }

            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (IOException e) {
            log.error("OpenAI API 요청 중 예외 발생", e);
            return "OpenAI API 요청 중 오류가 발생했습니다.";
        }
    }
}

