package org.funding.openAi.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Param;
import org.funding.fund.vo.FundVO;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dto.*;
import org.funding.openAi.service.ChatService;
import org.funding.openAi.service.FundAIService;
import org.funding.openAi.service.OpenVisionService;
import org.funding.security.util.Auth;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Auth
    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponseDTO ask(@RequestBody ChatRequestDTO chatRequestDTO,
                               HttpServletRequest request) {
        return chatService.ask(chatRequestDTO);
    }

    // 펀딩 ai 요약하기
    @Auth
    @PostMapping(value = "/fund", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SummaryFundResponseDTO summaryFund(@RequestBody SummaryFundRequestDTO summaryFundRequestDTO,
                                              HttpServletRequest request) {
        return chatService.summaryFund(summaryFundRequestDTO);
    }

    // 현재 펀딩과 비슷한 펀딩 추천받기
    @Auth
    @GetMapping(value = "/{fundId}/ai-recommend")
    public List<FundVO> recommendSimilarFundsByAI(@PathVariable Long fundId,
                                                  HttpServletRequest request) {
        return fundAIService.aiSimilar(fundId);
    }

    // 이미지 분석
    @Auth
    @PostMapping("/analyze-image")
    public ResponseEntity<VisionResponseDTO> analyzeImage(@RequestParam("imageUrl") String imageUrl,
                                               @RequestParam("prompt") String prompt,
                                               HttpServletRequest request) {
        VisionResponseDTO visionResponse = openVisionService.analyzeImage(imageUrl, prompt);
        return ResponseEntity.ok(visionResponse);
    }

}
