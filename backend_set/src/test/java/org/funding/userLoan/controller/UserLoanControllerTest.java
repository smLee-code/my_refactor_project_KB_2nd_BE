package org.funding.userLoan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.funding.user.dao.MemberDAO;
import org.funding.user.dto.MyPageResponseDTO;
import org.funding.user.service.MyPageService;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.funding.userLoan.dto.*;
import org.funding.userLoan.service.UserLoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserLoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserLoanService userLoanService;
    @Mock
    private MemberDAO memberDAO;
    @Mock
    private MyPageService myPageService;

    @InjectMocks
    private UserLoanController userLoanController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;
    private final Long MOCK_ADMIN_ID = 99L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userLoanController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("[사용자] 대출 신청 API")
    void applyUserLoan() throws Exception {
        // given
        Long fundId = 10L;
        UserLoanRequestDTO requestDTO = new UserLoanRequestDTO();
        requestDTO.setLoanAmount(1000000);

        UserLoanResponseDTO responseDTO = new UserLoanResponseDTO();
        responseDTO.setUserName("테스트유저");
        responseDTO.setLoanAmount(1000000);

        given(userLoanService.applyLoan(anyLong(), any(UserLoanRequestDTO.class), anyLong()))
                .willReturn(ResponseEntity.ok(responseDTO));

        // when & then
        mockMvc.perform(post("/api/user-loan/{id}", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("테스트유저"));
    }

    @Test
    @DisplayName("[사용자] 대출 신청 취소 API")
    void cancelUserLoan() throws Exception {
        // given
        Long fundId = 10L;

        // when & then
        mockMvc.perform(delete("/api/user-loan/{id}", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("정상적으로 취소가 완료되었습니다."));

        verify(userLoanService).cancelLoan(fundId, MOCK_USER_ID);
    }

    @Test
    @DisplayName("[관리자] 대출 신청 승인 API")
    void approveUserLoan() throws Exception {
        // given
        ApproveUserLoanRequestDTO requestDTO = new ApproveUserLoanRequestDTO();
        requestDTO.setUserLoanId(20L);
        String successMessage = "허가 완료";
        given(userLoanService.approveLoan(any(ApproveUserLoanRequestDTO.class), anyLong())).willReturn(successMessage);

        // when & then
        mockMvc.perform(patch("/api/user-loan/approve")
                        .requestAttr("userId", MOCK_ADMIN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("[사용자] 내가 신청한 대출 목록 조회 API")
    void getMyAllLoans() throws Exception {
        // given
        UserLoanDetailDTO detailDTO = UserLoanDetailDTO.builder().loanName("테스트 대출").build();
        List<UserLoanDetailDTO> responseList = Collections.singletonList(detailDTO);
        given(userLoanService.getAllUserLoans(MOCK_USER_ID)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/user-loan/user/all/v2")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanName").value("테스트 대출"));
    }

    @Test
    @DisplayName("[관리자] 특정 대출의 신청자 목록 조회 API")
    void getLoanApplications() throws Exception {
        // given
        Long fundId = 10L;
        UserLoanApplicationDTO applicationDTO = UserLoanApplicationDTO.builder().username("신청자").build();
        List<UserLoanApplicationDTO> responseList = Collections.singletonList(applicationDTO);
        given(userLoanService.getApplicationsForLoan(anyLong(), anyString(), anyLong())).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/user-loan/loan/{fundId}/applications", fundId)
                        .param("status", "PENDING")
                        .requestAttr("userId", MOCK_ADMIN_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("신청자"));
    }


    @Test
    @DisplayName("[관리자] 대출 신청자 상세 정보 조회 API - 성공")
    void getUserDetails_successAsAdmin() throws Exception {
        // given
        Long targetUserId = 5L;

        // 1. 관리자 역할을 가진 가짜 MemberVO 객체 생성
        MemberVO adminMember = new MemberVO();
        adminMember.setRole(Role.ROLE_ADMIN);

        MyPageResponseDTO myPageDTO = MyPageResponseDTO.builder().username("조회대상사용자").build();

        // 2. memberDAO가 MOCK_ADMIN_ID로 호출되면, 위에서 만든 관리자 객체를 반환하도록 설정
        given(memberDAO.findById(MOCK_ADMIN_ID)).willReturn(adminMember);

        // 3. myPageService는 정상적으로 DTO를 반환하도록 설정
        given(myPageService.getMyPageInfo(targetUserId)).willReturn(myPageDTO);

        // when & then
        mockMvc.perform(get("/api/user-loan/users/{userId}", targetUserId)
                        .requestAttr("userId", MOCK_ADMIN_ID) // 요청자를 MOCK_ADMIN_ID로 설정
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // 에러 없이 200 OK가 나와야 함
                .andExpect(jsonPath("$.username").value("조회대상사용자"));
    }
}