package org.funding.openAi.client;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OpenAIVisionClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json");

    @Value("${openai.api.key}")
    private String apiKey;

    public String analyzeImageWithPrompt(String imageUrl, String prompt) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray contentArray = new JSONArray();

            JSONObject textObject = new JSONObject();
            textObject.put("type", "text");
            textObject.put("text", prompt);
            contentArray.put(textObject);

            JSONObject imageObject = new JSONObject();
            imageObject.put("type", "image_url");
            JSONObject imageUrlObject = new JSONObject();
            imageUrlObject.put("url", imageUrl);
            imageObject.put("image_url", imageUrlObject);

            contentArray.put(imageObject);

            userMessage.put("content", contentArray);
            messages.put(userMessage);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4o");
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 1000);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("GPT Vision 호출 실패: HTTP {}", response.code());
                log.error("응답 바디: {}", response.body().string());
                return "GPT-4 Vision 호출 실패" + response.code();
            }

            String responseBody = response.body().string();
            log.info("GPT-4 Vision 응답: {}", responseBody);

            JSONObject json = new JSONObject(responseBody);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (Exception e) {
            log.error("GPT-4 Vision API 요청 중 예외 발생", e);
            return "GPT-4 Vision API 요청 중 예외 발생!";
        }
    }
}

