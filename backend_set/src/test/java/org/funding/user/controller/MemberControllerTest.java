package org.funding.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.security.util.JwtProcessor;
import org.funding.user.dto.MemberLoginDTO;
import org.funding.user.dto.MemberLoginResponseDTO;
import org.funding.user.dto.MemberSignupDTO;
import org.funding.user.service.MemberService;
import org.funding.user.vo.enumType.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup() throws Exception {
        // given
        MemberSignupDTO signupDTO = new MemberSignupDTO("testuser", "password123", "test@example.com", "테스트닉네임", "010-1234-5678", Collections.singletonList(1L));

        // when & then
        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("회원 가입 성공"));

        verify(memberService).signup(any(MemberSignupDTO.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        // given
        MemberLoginDTO loginDTO = new MemberLoginDTO("test@example.com", "password123");
        MemberLoginResponseDTO responseDTO = new MemberLoginResponseDTO("test.jwt.token", 1L, Role.ROLE_NORMAL);

        // MemberService의 login 메서드는 MemberLoginResponseDTO를 반환한다고 가정
        given(memberService.login(anyString(), anyString())).willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(responseDTO.getToken()))
                .andExpect(jsonPath("$.userId").value(responseDTO.getUserId()));
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복되지 않음")
    void checkEmailDuplicate_notDuplicated() throws Exception {
        // given
        String email = "new@example.com";
        given(memberService.checkEmailDuplicate(email)).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/member/duplicated/email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복됨")
    void checkNicknameDuplicate_isDuplicated() throws Exception {
        // given
        String nickname = "existingNickname";
        given(memberService.checkNicknameDuplicate(nickname)).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/member/duplicated/nickname")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}