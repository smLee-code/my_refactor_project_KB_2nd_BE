package org.funding.openAi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.fund.vo.FundVO;
import org.funding.openAi.controller.OpenAiController;
import org.funding.openAi.dto.*;
import org.funding.openAi.service.ChatService;
import org.funding.openAi.service.FundAIService;
import org.funding.openAi.service.OpenVisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OpenAiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;
    @Mock
    private FundAIService fundAIService;
    @Mock
    private OpenVisionService openVisionService;

    @InjectMocks
    private OpenAiController openAiController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(openAiController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("범용 AI 채팅 API")
    void ask() throws Exception {
        // given
        ChatRequestDTO requestDTO = new ChatRequestDTO();
        requestDTO.setPrompt("오늘 날씨 어때?");

        ChatResponseDTO responseDTO = new ChatResponseDTO();
        responseDTO.setResponse("오늘 서울은 맑고 화창한 날씨가 예상됩니다.");

        given(chatService.ask(any(ChatRequestDTO.class))).willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/ai/ask")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(responseDTO.getResponse()));
    }

    @Test
    @DisplayName("펀딩 상품 AI 설명 API")
    void explainFundByAI() throws Exception {
        // given
        Long fundId = 10L;

        AIExplainFundRequestDTO requestDTO = new AIExplainFundRequestDTO();
        requestDTO.setFundId(fundId);

        AIExplainFundResponseDTO responseDTO = new AIExplainFundResponseDTO(
                "이건 ~하는 서비스야",
                List.of("특징1", "특징2"),
                List.of("장점1", "장점2"),
                List.of("단점1", "단점2")
        );
        given(chatService.explainFund(anyLong(), anyLong())).willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/ai/fund/ai-explain")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.introduction").value(responseDTO.getIntroduction()))
                .andExpect(jsonPath("$.features[0]").value("특징1"));
    }

    @Test
    @DisplayName("유사 펀딩 추천 API")
    void recommendSimilarFundsByAI() throws Exception {
        // given
        Long fundId = 10L;
        FundVO fundVO = FundVO.builder().fundId(11L).build();
        List<FundVO> responseList = Collections.singletonList(fundVO);
        given(fundAIService.aiSimilar(fundId)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/ai/{fundId}/ai-recommend", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundId").value(11L));
    }

    @Test
    @DisplayName("사용자 맞춤 펀딩 추천 API")
    void recommendFundsForUser() throws Exception {
        // given
        FundVO fundVO = FundVO.builder().fundId(12L).build();
        List<FundVO> responseList = Collections.singletonList(fundVO);
        given(fundAIService.recommendForUser(MOCK_USER_ID)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/ai/ai-recommend")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundId").value(12L));
    }

    @Test
    @DisplayName("이미지 분석 (챌린지 인증) API")
    void analyzeImage() throws Exception {
        // given
        String imageUrl = "http://example.com/image.jpg";
        String prompt = "웃고 있는 강아지가 있는지 확인해줘";
        // ★★★ DTO 정보 반영 ★★★
        VisionResponseDTO responseDTO = new VisionResponseDTO();
        responseDTO.setScore(95);
        responseDTO.setReason("사진 중앙에 활짝 웃고 있는 골든 리트리버가 있습니다.");

        given(openVisionService.analyzeImage(anyString(), anyString())).willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/ai/analyze-image")
                        .param("imageUrl", imageUrl)
                        .param("prompt", prompt)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(95))
                .andExpect(jsonPath("$.reason").value("사진 중앙에 활짝 웃고 있는 골든 리트리버가 있습니다."));
    }
}