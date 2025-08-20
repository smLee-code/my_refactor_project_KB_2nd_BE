package org.funding.retryVotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.retryVotes.controller.RetryVotesController;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.dto.MyVotedFundDTO;
import org.funding.retryVotes.service.RetryVotesService;
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
class RetryVotesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RetryVotesService retryVotesService;

    @InjectMocks
    private RetryVotesController retryVotesController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(retryVotesController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("재출시 투표하기 API")
    void doVote() throws Exception {
        // given
        DoVoteRequestDTO requestDTO = new DoVoteRequestDTO(10L);
        String successMessage = "투표가 정상적으로 등록되었습니다.";
        given(retryVotesService.doVote(any(DoVoteRequestDTO.class), anyLong())).willReturn(successMessage);

        // when & then
        mockMvc.perform(post("/api/retryVotes/do")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("재출시 투표 취소 API")
    void deleteVote() throws Exception {
        // given
        DoVoteRequestDTO requestDTO = new DoVoteRequestDTO(10L);
        String successMessage = "투표가 정상적으로 취소되었습니다";
        given(retryVotesService.deleteVote(any(DoVoteRequestDTO.class), anyLong())).willReturn(successMessage);

        // when & then
        mockMvc.perform(delete("/api/retryVotes/cancel")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));

    }

    @Test
    @DisplayName("내가 투표한 펀딩 목록 조회 API")
    void getMyAllVotedFunds() throws Exception {
        // given
        MyVotedFundDTO votedFund = new MyVotedFundDTO();
        votedFund.setProductName("다시 만나고 싶은 펀딩");
        List<MyVotedFundDTO> responseList = Collections.singletonList(votedFund);
        given(retryVotesService.findMyVotedFunds(MOCK_USER_ID)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/retryVotes/my-fund/list")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productName").value("다시 만나고 싶은 펀딩"));
    }
}