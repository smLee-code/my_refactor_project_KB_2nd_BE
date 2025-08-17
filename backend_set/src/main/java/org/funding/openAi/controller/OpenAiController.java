package org.funding.openAi.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.funding.fund.vo.FundVO;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.OpenAiException;
import org.funding.openAi.dto.*;
import org.funding.openAi.service.ChatService;
import org.funding.openAi.service.FundAIService;
import org.funding.openAi.service.OpenVisionService;
import org.funding.security.util.Auth;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
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

    // 펀딩 ai 요약하기 version 2
    @Auth
    @PostMapping("/fund/ai-explain")
    public ResponseEntity<AIExplainFundResponseDTO> explainFundByAI(
            @RequestBody AIExplainFundRequestDTO request,
            HttpServletRequest servletRequest) throws JsonProcessingException {

        // getFundDetail에서 isJoined 여부를 확인하기 위해 userId를 전달할 수 있습니다.
        Long userId = (Long) servletRequest.getAttribute("userId");
        AIExplainFundResponseDTO response = chatService.explainFund(request.getFundId(), userId);
        return ResponseEntity.ok(response);
    }

    // 현재 펀딩과 비슷한 펀딩 추천받기
    @Auth
    @GetMapping(value = "/{fundId}/ai-recommend")
    public List<FundVO> recommendSimilarFundsByAI(@PathVariable Long fundId,
                                                  HttpServletRequest request) {
        return fundAIService.aiSimilar(fundId);
    }

    // 사용자 맞춤형 펀딩 추천
    @Auth
    @GetMapping("/ai-recommend")
    public ResponseEntity<List<FundVO>> recommendFundsForUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new OpenAiException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<FundVO> recommendedFunds = fundAIService.recommendForUser(userId);
        return ResponseEntity.ok(recommendedFunds);
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
