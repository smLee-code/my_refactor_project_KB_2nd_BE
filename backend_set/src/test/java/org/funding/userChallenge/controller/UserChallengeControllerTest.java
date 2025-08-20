package org.funding.userChallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.S3.service.S3ImageService;
import org.funding.userChallenge.controller.UserChallengeController;
import org.funding.userChallenge.dto.ChallengeDetailResponseDTO;
import org.funding.userChallenge.dto.UserChallengeDetailDTO;
import org.funding.userChallenge.service.UserChallengeService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserChallengeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserChallengeService userChallengeService;
    @Mock
    private S3ImageService s3ImageService;
    @InjectMocks
    private UserChallengeController userChallengeController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // JSON과 String 컨버터를 모두 등록
        mockMvc = MockMvcBuilders.standaloneSetup(userChallengeController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("챌린지 참여(가입) API")
    void applyChallenge() throws Exception {
        Long fundId = 10L;
        mockMvc.perform(post("/api/user-challenge/{id}", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가입이 완료되었습니다"));
        verify(userChallengeService).applyChallenge(fundId, MOCK_USER_ID);
    }

    @Test
    @DisplayName("챌린지 참여 취소 API")
    void deleteChallenge() throws Exception {
        Long fundId = 10L;
        mockMvc.perform(delete("/api/user-challenge/{id}", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("정상적으로 챌린지에서 취소되었습니다."));
        verify(userChallengeService).deleteChallenge(fundId, MOCK_USER_ID);
    }

    @Test
    @DisplayName("일일 챌린지 인증 API (이미지 업로드)")
    void verifyChallenge() throws Exception {
        Long userChallengeId = 20L;
        String dateString = "2025-08-19";
        String mockImageUrl = "https://s3.amazonaws.com/bucket/image.jpg";
        MockMultipartFile mockFile = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes(StandardCharsets.UTF_8));
        given(s3ImageService.uploadSingleImageAndGetUrl(any(MockMultipartFile.class))).willReturn(mockImageUrl);

        mockMvc.perform(multipart("/api/user-challenge/{id}/verify", userChallengeId)
                        .file(mockFile)
                        .param("date", dateString)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("인증 완료"));
        verify(userChallengeService).verifyDailyChallenge(userChallengeId, MOCK_USER_ID, mockImageUrl, LocalDate.parse(dateString));
    }

    @Test
    @DisplayName("내가 참여한 모든 챌린지 조회 API")
    void getAllMyChallenges() throws Exception {
        UserChallengeDetailDTO challengeDTO = new UserChallengeDetailDTO();
        challengeDTO.setChallengeName("아침 6시 기상 챌린지");
        List<UserChallengeDetailDTO> mockResponse = Collections.singletonList(challengeDTO);
        given(userChallengeService.findMyChallenges(MOCK_USER_ID)).willReturn(mockResponse);

        mockMvc.perform(get("/api/user-challenge/user/all/v2")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].challengeName").value("아침 6시 기상 챌린지"));
    }

    @Test
    @DisplayName("참여중인 챌린지 상세 조회 API")
    void getChallengeDetail() throws Exception {
        Long userChallengeId = 20L;
        ChallengeDetailResponseDTO mockResponse = new ChallengeDetailResponseDTO();
        UserChallengeDetailDTO detailDTO = new UserChallengeDetailDTO();
        detailDTO.setChallengeName("매일 만 보 걷기");
        mockResponse.setChallengeInfo(detailDTO);
        mockResponse.setDailyLogs(Collections.emptyList());
        given(userChallengeService.getChallengeDetails(userChallengeId)).willReturn(mockResponse);

        mockMvc.perform(get("/api/user-challenge/{userChallengeId}", userChallengeId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.challengeInfo.challengeName").value("매일 만 보 걷기"));
    }
}