package org.funding.userSaving.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.userSaving.dto.UserSavingDetailDTO;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.service.UserSavingService;
import org.funding.userSaving.vo.UserSavingVO;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserSavingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserSavingService userSavingService;

    @InjectMocks
    private UserSavingController userSavingController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // JSON과 String 응답을 모두 처리할 수 있도록 컨버터를 설정
        mockMvc = MockMvcBuilders.standaloneSetup(userSavingController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("저축 상품 가입 API")
    void applySaving() throws Exception {
        // given
        Long fundId = 10L;
        UserSavingRequestDTO requestDTO = new UserSavingRequestDTO();
        requestDTO.setSavingAmount(50000);
        String successMessage = "정상적으로 저축에 가입하셨습니다.";

        given(userSavingService.applySaving(anyLong(), any(UserSavingRequestDTO.class), anyLong())).willReturn(successMessage);

        // when & then
        mockMvc.perform(post("/api/user-saving/{id}", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("저축 상품 해지 API")
    void cancelSaving() throws Exception {
        // given
        Long userSavingId = 20L;
        String successMessage = "정상적으로 해지되었습니다.";
        given(userSavingService.cancelSaving(userSavingId, MOCK_USER_ID)).willReturn(successMessage);

        // when & then
        mockMvc.perform(delete("/api/user-saving/cancel/{id}", userSavingId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("단일 저축 가입 내역 조회 API")
    void getUserSaving() throws Exception {
        // given
        Long userSavingId = 20L;
        UserSavingVO responseVO = new UserSavingVO();
        responseVO.setUserSavingId(userSavingId);
        responseVO.setUserId(MOCK_USER_ID);
        responseVO.setSavingAmount(50000);

        given(userSavingService.findById(userSavingId)).willReturn(responseVO);

        // when & then
        mockMvc.perform(get("/api/user-saving/{id}", userSavingId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userSavingId").value(userSavingId))
                .andExpect(jsonPath("$.savingAmount").value(50000));
    }

    @Test
    @DisplayName("나의 전체 저축 내역 조회 (v2) API")
    void getMyAllSavings() throws Exception {
        // given
        UserSavingDetailDTO detailDTO = new UserSavingDetailDTO();
        detailDTO.setSavingName("티끌모아 태산 저축");
        detailDTO.setSavingAmount(100000);
        List<UserSavingDetailDTO> responseList = Collections.singletonList(detailDTO);

        given(userSavingService.findMySavings(MOCK_USER_ID)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/user-saving/user/all/v2")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].savingName").value("티끌모아 태산 저축"));
    }
}