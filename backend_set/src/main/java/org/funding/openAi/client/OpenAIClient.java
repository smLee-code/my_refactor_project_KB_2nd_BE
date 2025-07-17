package org.funding.openAi.client;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OpenAIClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String API_KEY;


    public String askOpenAI(String prompt) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            requestBody.put("messages", messages);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization",API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), mediaType))
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while calling OpenAI API";
        }
    }
}
