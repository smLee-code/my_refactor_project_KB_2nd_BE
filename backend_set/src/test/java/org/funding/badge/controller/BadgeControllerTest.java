package org.funding.badge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.funding.badge.service.BadgeService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BadgeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BadgeService badgeService;

    @InjectMocks
    private BadgeController badgeController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;
    private final Long MOCK_ADMIN_ID = 99L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(badgeController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("[관리자] 뱃지 생성 API")
    void createBadge() throws Exception {
        // given
        CreateBadgeDTO requestDTO = new CreateBadgeDTO("테스트 뱃지", "테스트 설명", "DONATED");
        // void를 반환하는 서비스는 doNothing()으로 Mocking
        doNothing().when(badgeService).createBadge(any(CreateBadgeDTO.class), anyLong());

        // when & then
        mockMvc.perform(post("/api/badge/create")
                        .requestAttr("userId", MOCK_ADMIN_ID) // 관리자 ID로 요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("뱃지가 정상적으로 등록되었습니다."));

        verify(badgeService).createBadge(any(CreateBadgeDTO.class), anyLong());
    }

    @Test
    @DisplayName("[관리자] 뱃지 수정 API")
    void updateBadge() throws Exception {
        // given
        Long badgeId = 1L;
        UpdateBadgeDTO requestDTO = new UpdateBadgeDTO("수정된 뱃지", "수정된 설명", "COMPLETED_FUNDED_PROJECT");
        doNothing().when(badgeService).updateBadge(any(UpdateBadgeDTO.class), anyLong(), anyLong());

        // when & then
        mockMvc.perform(put("/api/badge/{id}", badgeId)
                        .requestAttr("userId", MOCK_ADMIN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("뱃지가 정상적으로 업데이트 되었습니다"));
    }

    @Test
    @DisplayName("[관리자] 뱃지 삭제 API")
    void deleteBadge() throws Exception {
        // given
        Long badgeId = 1L;
        doNothing().when(badgeService).deleteBadge(anyLong(), anyLong());

        // when & then
        mockMvc.perform(delete("/api/badge/{id}", badgeId)
                        .requestAttr("userId", MOCK_ADMIN_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("뱃지가 정상적으로 삭제되었습니다."));

        verify(badgeService).deleteBadge(badgeId, MOCK_ADMIN_ID);
    }

    @Test
    @DisplayName("뱃지 단건 조회 API")
    void readBadge() throws Exception {
        // given
        Long badgeId = 1L;
        BadgeResponseDTO responseDTO = new BadgeResponseDTO(badgeId, "테스트 뱃지", "설명", "DONATED");
        given(badgeService.getBadge(badgeId)).willReturn(responseDTO);

        // when & then
        mockMvc.perform(get("/api/badge/{id}", badgeId)
                        .requestAttr("userId", MOCK_USER_ID) // 일반 유저도 조회 가능
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트 뱃지"));
    }

    @Test
    @DisplayName("뱃지 전체 조회 API")
    void getAllBadges() throws Exception {
        // given
        BadgeResponseDTO responseDTO = new BadgeResponseDTO(1L, "테스트 뱃지", "설명", "DONATED");
        List<BadgeResponseDTO> responseList = Collections.singletonList(responseDTO);
        given(badgeService.getAllBadges()).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/badge/all/badge")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테스트 뱃지"));
    }

    @Test
    @DisplayName("사용자가 보유한 뱃지 조회 API")
    void getUserBadges() throws Exception {
        // given
        BadgeResponseDTO responseDTO = new BadgeResponseDTO(1L, "기부천사", "기부한 사용자에게 부여", "DONATED");
        List<BadgeResponseDTO> responseList = Collections.singletonList(responseDTO);
        given(badgeService.getUserBadges(MOCK_USER_ID)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/badge/user")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("기부천사"));

        verify(badgeService).getUserBadges(MOCK_USER_ID);
    }
}