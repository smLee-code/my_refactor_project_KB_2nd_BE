package org.funding.userDonation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.userDonation.controller.UserDonationController;
import org.funding.userDonation.dto.DonateRequestDTO;
import org.funding.userDonation.dto.DonateResponseDTO;
import org.funding.userDonation.dto.UserDonationDetailDTO;
import org.funding.userDonation.service.UserDonationService;
import org.funding.userDonation.vo.UserDonationVO;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserDonationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserDonationService userDonationService;

    @InjectMocks
    private UserDonationController userDonationController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // JSON과 String 응답을 모두 처리할 수 있도록 컨버터를 설정
        mockMvc = MockMvcBuilders.standaloneSetup(userDonationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("기부하기 API")
    void donate() throws Exception {
        // given
        // ★★★★★ DTO 정보를 반영하여 setter로 객체를 생성합니다 ★★★★★
        DonateRequestDTO requestDTO = new DonateRequestDTO();
        requestDTO.setFundId(10L);
        requestDTO.setDonateAmount(5000);
        requestDTO.setAnonymous(false);

        DonateResponseDTO responseDTO = new DonateResponseDTO();
        responseDTO.setUserName("테스트유저");
        responseDTO.setDonateAmount(5000);
        // ★★★★★ 여기까지 수정되었습니다 ★★★★★

        given(userDonationService.donate(any(DonateRequestDTO.class), anyLong())).willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/user-donation/donation")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("테스트유저"))
                .andExpect(jsonPath("$.donateAmount").value(5000));
    }

    @Test
    @DisplayName("단일 기부 내역 상세 조회 API")
    void getDonation() throws Exception {
        // given
        Long userDonationId = 1L;
        UserDonationVO donationVO = new UserDonationVO();
        donationVO.setUserDonationId(userDonationId);
        donationVO.setDonationAmount(10000);

        given(userDonationService.getDonation(userDonationId)).willReturn(donationVO);

        // when & then
        mockMvc.perform(get("/api/user-donation/donation/{id}", userDonationId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDonationId").value(userDonationId))
                .andExpect(jsonPath("$.donationAmount").value(10000));
    }

    @Test
    @DisplayName("나의 전체 기부 내역 조회 API")
    void getAllDonations() throws Exception {
        // given
        UserDonationVO donationVO = new UserDonationVO();
        donationVO.setUserDonationId(1L);
        List<UserDonationVO> donationList = Collections.singletonList(donationVO);

        given(userDonationService.getAllDonations(MOCK_USER_ID)).willReturn(donationList);

        // when & then
        mockMvc.perform(get("/api/user-donation/donation-history")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("기부 내역 수정 API")
    void updateDonation() throws Exception {
        // given
        Long userDonationId = 1L;
        DonateRequestDTO requestDTO = new DonateRequestDTO();
        requestDTO.setFundId(10L);
        requestDTO.setDonateAmount(15000);
        requestDTO.setAnonymous(true);
        String successMessage = "기부 내역이 수정되었습니다.";

        given(userDonationService.updateDonation(anyLong(), any(DonateRequestDTO.class), anyLong())).willReturn(successMessage);

        // when & then
        mockMvc.perform(patch("/api/user-donation/{id}", userDonationId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }



    @Test
    @DisplayName("기부 내역 삭제 API")
    void deleteDonation() throws Exception {
        // given
        Long userDonationId = 1L;
        String successMessage = "정상적으로 기부 내역이 삭제되었습니다.";

        given(userDonationService.deleteDonation(userDonationId, MOCK_USER_ID)).willReturn(successMessage);

        // when & then
        mockMvc.perform(delete("/api/user-donation/{id}", userDonationId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("내가 참여한 모든 기부 펀딩 조회 API")
    void getMyAllDonations() throws Exception {
        // given
        UserDonationDetailDTO detailDTO = new UserDonationDetailDTO();
        detailDTO.setDonationName("유기견 보호소 기부");
        List<UserDonationDetailDTO> donationList = Collections.singletonList(detailDTO);

        given(userDonationService.findMyDonations(MOCK_USER_ID)).willReturn(donationList);

        // when & then
        mockMvc.perform(get("/api/user-donation/user/all/v2")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].donationName").value("유기견 보호소 기부"));
    }
}