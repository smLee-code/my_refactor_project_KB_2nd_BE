package org.funding.openAi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.funding.openAi.dto.VisionResponseDTO;
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

    public VisionResponseDTO analyzeImageWithPrompt(String imageUrl, String prompt) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray contentArray = new JSONArray();

            JSONObject textObject = new JSONObject();
            textObject.put("type", "text");
            String refinedPrompt = String.format(
                    "당신은 자동 이미지 검증 시스템입니다. 주어진 이미지가 아래의 '검증 조건'을 만족하는지 판단하고, 결과를 반드시 다음 JSON 형식으로만 응답해주세요. " +
                            "다른 설명은 절대 추가하지 마세요. JSON 객체 형식: {\"score\": [0-100 사이의 정수 점수], \"reason\": \"[판단 근거에 대한 한글 설명]\"}. " +
                            "조건에 완벽히 부합하면 100점에 가깝게, 완전히 다르면 0점에 가깝게 점수를 부여하세요. --- 검증 조건: %s",
                    prompt
            );
            textObject.put("text", refinedPrompt);
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
                throw new RuntimeException("이미지 검증 처리중 에러 발생");
            }

            String responseBody = response.body().string();
            log.info("GPT-4 Vision 응답: {}", responseBody);

            JSONObject json = new JSONObject(responseBody);
            String content = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return mapper.readValue(content, VisionResponseDTO.class);
        } catch (Exception e) {
            log.error("GPT-4 Vision API 요청 중 예외 발생", e);
            throw new RuntimeException("이미지 검증 처리중 에러 발생");
        }
    }
}

