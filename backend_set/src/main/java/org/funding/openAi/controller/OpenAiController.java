package org.funding.openAi.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "AI 기능 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Log4j2
public class OpenAiController {

    private final ChatService chatService;
    private final FundAIService fundAIService;
    private final OpenVisionService openVisionService;

    @ApiOperation(value = "범용 AI 채팅", notes = "사용자의 질문에 대해 AI가 답변합니다.")
    @Auth
    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponseDTO ask(@RequestBody ChatRequestDTO chatRequestDTO,
                               HttpServletRequest request) {
        return chatService.ask(chatRequestDTO);
    }

    @ApiOperation(value = "펀딩 상품 정보 요약 (구버전)", notes = "입력된 펀딩 정보를 바탕으로 구조화된 데이터를 추출합니다.")
    @Auth
    @PostMapping(value = "/fund", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SummaryFundResponseDTO summaryFund(@RequestBody SummaryFundRequestDTO summaryFundRequestDTO,
                                              HttpServletRequest request) {
        return chatService.summaryFund(summaryFundRequestDTO);
    }

    @ApiOperation(value = "펀딩 상품 AI 설명 (신버전)", notes = "fundId를 받아 해당 펀딩의 장단점, 특징 등을 친구처럼 설명해줍니다.")
    @Auth
    @PostMapping("/fund/ai-explain")
    public ResponseEntity<AIExplainFundResponseDTO> explainFundByAI(
            @RequestBody AIExplainFundRequestDTO request,
            HttpServletRequest servletRequest) throws JsonProcessingException {
        Long userId = (Long) servletRequest.getAttribute("userId");
        AIExplainFundResponseDTO response = chatService.explainFund(request.getFundId(), userId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "유사 펀딩 추천", notes = "특정 펀딩(fundId)과 가장 유사한 다른 펀딩 목록을 AI가 추천합니다.")
    @Auth
    @GetMapping(value = "/{fundId}/ai-recommend")
    public List<FundVO> recommendSimilarFundsByAI(
            @ApiParam(value = "기준이 될 펀딩 ID", required = true, example = "1") @PathVariable Long fundId,
            HttpServletRequest request) {
        return fundAIService.aiSimilar(fundId);
    }

    @ApiOperation(value = "사용자 맞춤 펀딩 추천", notes = "로그인한 사용자의 관심 키워드를 기반으로 개인화된 펀딩 목록을 AI가 추천합니다.")
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

    @ApiOperation(value = "이미지 분석 (챌린지 인증)", notes = "주어진 이미지 URL과 검증 조건을 바탕으로 이미지를 분석하고 점수와 근거를 반환합니다.")
    @Auth
    @PostMapping("/analyze-image")
    public ResponseEntity<VisionResponseDTO> analyzeImage(
            @ApiParam(value = "분석할 이미지의 URL", required = true) @RequestParam("imageUrl") String imageUrl,
            @ApiParam(value = "이미지 검증 조건(프롬프트)", required = true) @RequestParam("prompt") String prompt,
            HttpServletRequest request) {
        VisionResponseDTO visionResponse = openVisionService.analyzeImage(imageUrl, prompt);
        return ResponseEntity.ok(visionResponse);
    }
}