package org.funding.votes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VotesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VotesService votesService;

    @InjectMocks
    private VotesController votesController;

    private ObjectMapper objectMapper;
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(votesController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("사용자의 투표 여부 확인 API")
    void hasVoted() throws Exception {
        // given
        Long projectId = 10L;
        given(votesService.hasVoted(any(VotesRequestDTO.class))).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/votes/{projectId}", projectId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("프로젝트 투표 취소 API")
    void cancelVote() throws Exception {
        // given
        Long projectId = 10L;
        doNothing().when(votesService).deleteVotes(anyLong(), anyLong());

        // when & then
        mockMvc.perform(delete("/api/votes")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(projectId)))
                .andDo(print())
                .andExpect(status().isNoContent()); // 204 No Content

        verify(votesService).deleteVotes(projectId, MOCK_USER_ID);
    }

    @Test
    @DisplayName("내가 투표한 프로젝트 목록 조회 API")
    void findVotedProjects() throws Exception {
        // given
        List<Long> votedProjectIds = Collections.singletonList(10L);
        given(votesService.findVotedProjects(MOCK_USER_ID)).willReturn(votedProjectIds);

        // when & then
        mockMvc.perform(get("/api/votes/my-votes")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(10L));
    }

    @Test
    @DisplayName("프로젝트 투표 수 조회 API")
    void countVotes() throws Exception {
        // given
        Long projectId = 10L;
        Long voteCount = 123L;
        given(votesService.countVotes(projectId)).willReturn(voteCount);

        // when & then
        mockMvc.perform(get("/api/votes/{projectId}/count", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("123"));
    }

    @Test
    @DisplayName("프로젝트 투표/취소 토글 API - 투표 생성")
    void toggleVote_create() throws Exception {
        // given
        Long projectId = 10L;
        VotesResponseDTO responseDTO = new VotesResponseDTO();
        responseDTO.setVoteId(99L);
        responseDTO.setProjectId(projectId);
        responseDTO.setUserId(MOCK_USER_ID);
        responseDTO.setVoteTime(LocalDateTime.now());

        given(votesService.toggleVote(projectId, MOCK_USER_ID)).willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/votes/{projectId}", projectId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteId").value(99L));
    }

    @Test
    @DisplayName("프로젝트 투표/취소 토글 API - 투표 취소")
    void toggleVote_cancel() throws Exception {
        // given
        Long projectId = 10L;
        // 투표 취소 시 null을 반환하는 것으로 가정
        given(votesService.toggleVote(projectId, MOCK_USER_ID)).willReturn(null);

        // when & then
        mockMvc.perform(post("/api/votes/{projectId}", projectId)
                        .requestAttr("userId", MOCK_USER_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}