package org.funding.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.funding.user.dto.MemberLoginDTO;
import org.funding.user.dto.MemberSignupDTO;
import org.funding.user.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Log4j2
class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    // 회원가입 테스트 코드 작성
    @Test
    void signup() throws Exception {
        MemberSignupDTO dto = new MemberSignupDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("test1234");
        dto.setUsername("testuser");
        dto.setNickname("tester");
        dto.setPhoneNumber("01012345678");

        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("회원 가입 성공"));
    }

    // 로그인 테스트 코드 작성
    @Test
    void login() throws Exception {
        MemberLoginDTO dto = new MemberLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("test1234");

        when(memberService.login(anyString(), anyString())).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-jwt-token"));
    }
}