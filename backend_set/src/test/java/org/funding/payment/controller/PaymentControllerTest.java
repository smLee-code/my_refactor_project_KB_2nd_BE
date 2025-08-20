package org.funding.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.payment.controller.PaymentController;
import org.funding.payment.dto.PaymentCompleteRequestDTO;
import org.funding.payment.dto.PaymentCreateRequestDTO;
import org.funding.payment.service.PaymentService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("결제 정보 사전 생성 API - 성공")
    void createPayment_success() throws Exception {
        // given
        PaymentCreateRequestDTO requestDTO = new PaymentCreateRequestDTO(10L, 3000, null);
        Map<String, Object> responseMap = Map.of(
                "merchant_uid", "payment_123456789",
                "amount", 3000
        );
        given(paymentService.createPaymentOrder(any(PaymentCreateRequestDTO.class), anyLong())).willReturn(responseMap);

        // when & then
        mockMvc.perform(post("/api/payments/create")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchant_uid").value("payment_123456789"))
                .andExpect(jsonPath("$.amount").value(3000));
    }

    @Test
    @DisplayName("결제 정보 사전 생성 API - 실패")
    void createPayment_failure() throws Exception {
        // given
        PaymentCreateRequestDTO requestDTO = new PaymentCreateRequestDTO(10L, 3000, null);
        String errorMessage = "펀딩 정보를 찾을 수 없습니다.";
        given(paymentService.createPaymentOrder(any(PaymentCreateRequestDTO.class), anyLong()))
                .willThrow(new RuntimeException(errorMessage));

        // when & then
        mockMvc.perform(post("/api/payments/create")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 컨트롤러에서 badRequest()를 반환
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("결제 완료 및 검증 API - 성공")
    void completePayment_success() throws Exception {
        // given
        PaymentCompleteRequestDTO requestDTO = new PaymentCompleteRequestDTO("imp_1234", "merchant_1234");
        Map<String, Object> responseMap = Map.of(
                "success", true,
                "message", "결제 및 펀딩 참여가 완료되었습니다."
        );
        given(paymentService.completePayment(any(PaymentCompleteRequestDTO.class))).willReturn(responseMap);

        // when & then
        mockMvc.perform(post("/api/payments/complete")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("결제 및 펀딩 참여가 완료되었습니다."));
    }

    @Test
    @DisplayName("결제 완료 및 검증 API - 실패")
    void completePayment_failure() throws Exception {
        // given
        PaymentCompleteRequestDTO requestDTO = new PaymentCompleteRequestDTO("imp_1234", "merchant_1234");
        given(paymentService.completePayment(any(PaymentCompleteRequestDTO.class)))
                .willThrow(new RuntimeException("결제 검증 실패"));

        // when & then
        mockMvc.perform(post("/api/payments/complete")
                        .requestAttr("userId", MOCK_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("결제 처리 중 오류가 발생했습니다."));
    }
}