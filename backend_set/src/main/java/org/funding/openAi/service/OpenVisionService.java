package org.funding.openAi.service;

import lombok.RequiredArgsConstructor;
import org.funding.openAi.client.OpenAIVisionClient;
import org.funding.openAi.dto.VisionResponseDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenVisionService {

    private final OpenAIVisionClient openAIVisionClient;

    public VisionResponseDTO analyzeImage(String imageUrl, String prompt) {
        return openAIVisionClient.analyzeImageWithPrompt(imageUrl, prompt);
    }
}
