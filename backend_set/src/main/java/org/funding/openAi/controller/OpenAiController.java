package org.funding.openAi.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Param;
import org.funding.fund.vo.FundVO;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dto.ChatRequestDTO;
import org.funding.openAi.dto.ChatResponseDTO;
import org.funding.openAi.dto.SummaryFundRequestDTO;
import org.funding.openAi.dto.SummaryFundResponseDTO;
import org.funding.openAi.service.ChatService;
import org.funding.openAi.service.FundAIService;
import org.funding.openAi.service.OpenVisionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Log4j2
public class OpenAiController {

    private final ChatService chatService;
    private final FundAIService fundAIService;
    private final OpenVisionService openVisionService;

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponseDTO ask(@RequestBody ChatRequestDTO chatRequestDTO) {
        return chatService.ask(chatRequestDTO);
    }

    // 펀딩 ai 요약하기
    @PostMapping(value = "/fund", consumes = MediaType.APPLICATION_JSON_VALUE )
    public SummaryFundResponseDTO summaryFund(@RequestBody SummaryFundRequestDTO summaryFundRequestDTO) {
        return chatService.summaryFund(summaryFundRequestDTO);
    }

    // 현재 펀딩과 비슷한 펀딩 추천받기
    @GetMapping(value = "/{fundId}/ai-recommend")
    public List<FundVO> recommendSimilarFundsByAI(@PathVariable Long fundId) {
        return fundAIService.aiSimilar(fundId);
    }

    // 이미지 분석
    @PostMapping("/analyze-image")
    public ResponseEntity<String> analyzeImage(@RequestParam("imageUrl") String imageUrl, @RequestParam("prompt") String prompt) {
        String result = openVisionService.analyzeImage(imageUrl, prompt);
        return ResponseEntity.ok(result);
    }

}
