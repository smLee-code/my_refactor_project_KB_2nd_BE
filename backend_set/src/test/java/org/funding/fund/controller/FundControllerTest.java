package org.funding.fund.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.funding.fund.controller.FundController;
import org.funding.fund.dto.FundDetailResponseDTO;
import org.funding.fund.dto.FundListResponseDTO;
import org.funding.fund.dto.FundProductRequestDTO;
import org.funding.fund.dto.MyFundDetailDTO;
import org.funding.fund.service.FundService;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FundControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FundService fundService;

    @InjectMocks
    private FundController fundController;

    private ObjectMapper objectMapper;
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(fundController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("저축(Savings) 펀딩 생성 API - Multipart")
    void createSavingsFund() throws Exception {
        // given
        // JSON DTO 생성 및 문자열 변환
        FundProductRequestDTO.SavingsRequest savingsRequest = FundProductRequestDTO.SavingsRequest.builder()
                .name("저축펀딩")
                .launchDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();
        String savingInfoJson = objectMapper.writeValueAsString(savingsRequest);

        // JSON 파트를 위한 MockMultipartFile 생성 (Content-Type 명시)
        MockMultipartFile savingInfoPart = new MockMultipartFile(
                "savingInfo",
                "", // 파일 이름 (JSON 파트이므로 비워둠)
                "application/json", // 이거 지정안해줘서 테스트 코드 에러남
                savingInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        // 이미지 파일 파트 생성
        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image_content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/fund/create/savings")
                        .file(imageFile)
                        .file(savingInfoPart)
                        .accept(MediaType.TEXT_PLAIN)
                        .requestAttr("userId", MOCK_USER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("펀딩이 성공적으로 생성되었습니다."));
    }


    @Test
    @DisplayName("펀딩 목록 조회 API")
    void getFundsList() throws Exception {
        // given
        FundListResponseDTO fundDTO = FundListResponseDTO.builder().fundId(1L).name("테스트 펀딩").build();
        List<FundListResponseDTO> responseList = Collections.singletonList(fundDTO);

        given(fundService.getFundsByProgressAndType(ProgressType.Launch, FundType.Challenge)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/fund/list")
                        .param("progress", "Launch")
                        .param("fundType", "Challenge")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테스트 펀딩"));
    }

    @Test
    @DisplayName("펀딩 상세 조회 API")
    void getFundDetail() throws Exception {
        // given
        Long fundId = 1L;
        FundDetailResponseDTO responseDTO = FundDetailResponseDTO.builder()
                .fundId(fundId)
                .name("상세조회 펀딩")
                .participantCount(10)
                .build();

        given(fundService.getFundDetail(fundId, null)).willReturn(responseDTO);

        // when & then
        mockMvc.perform(get("/api/fund/{fundId}", fundId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상세조회 펀딩"))
                .andExpect(jsonPath("$.participantCount").value(10));
    }

    @Test
    @DisplayName("펀딩 삭제 API")
    void deleteFund() throws Exception {
        // given
        Long fundId = 1L;
        String successMessage = "펀딩이 성공적으로 삭제되었습니다.";
        given(fundService.deleteFund(fundId, MOCK_USER_ID)).willReturn(successMessage);

        // when & then
        mockMvc.perform(delete("/api/fund/{fundId}", fundId)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(successMessage));
    }

    @Test
    @DisplayName("내가 생성한 펀딩 목록 조회 API")
    void getMyAllCreatedFunds() throws Exception {
        // given
        MyFundDetailDTO myFundDTO = new MyFundDetailDTO();
        myFundDTO.setProductName("내가 만든 펀딩");
        List<MyFundDetailDTO> responseList = Collections.singletonList(myFundDTO);

        given(fundService.findMyCreatedFunds(MOCK_USER_ID, "Loan")).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/fund/my/fund/all")
                        .param("fundType", "Loan")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("내가 만든 펀딩"));
    }
}