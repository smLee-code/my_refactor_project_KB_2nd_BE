package org.funding.chatting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.chatting.controller.ChattingController;
import org.funding.chatting.dto.RealtimeChatResponseDTO;
import org.funding.chatting.service.ChattingService;
import org.funding.user.dao.MemberDAO;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChattingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChattingService chattingService;

    @Mock
    private MemberDAO memberDAO; // 컨트롤러의 의존성이므로 Mock으로 추가

    @InjectMocks
    private ChattingController chattingController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chattingController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("채팅 내역 조회 (HTTP) API")
    void getChatHistory() throws Exception {
        // given
        Long projectId = 10L;
        RealtimeChatResponseDTO chatMessage = RealtimeChatResponseDTO.builder()
                .id(1L)
                .projectId(projectId)
                .userId(MOCK_USER_ID)
                .nickname("테스트유저")
                .content("안녕하세요!")
                .timestamp(LocalDateTime.now())
                .build();
        List<RealtimeChatResponseDTO> responseList = Collections.singletonList(chatMessage);

        given(chattingService.getMessages(anyLong(), anyLong())).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/chat/history/{projectId}", projectId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].content").value("안녕하세요!"))
                .andExpect(jsonPath("$[0].nickname").value("테스트유저"));
    }
}