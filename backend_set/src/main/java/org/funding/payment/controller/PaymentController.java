package org.funding.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.payment.dto.PaymentCompleteRequestDTO;
import org.funding.payment.dto.PaymentCreateRequestDTO;
import org.funding.security.util.Auth;
import org.springframework.security.core.Authentication;
import org.funding.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Api(tags = "payments-controller", description = "Payment Controller")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;

    @Auth
    @PostMapping("/create")
    @ApiOperation(value = "결제 생성", notes = "결제를 위한 merchant_uid와 금액을 생성합니다.")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody PaymentCreateRequestDTO request,
            HttpServletRequest requestHeaders) {
        try {
            log.info("결제 정보 생성 요청 - fundId: {}", 
                request.getFundId());

            Long userId = (Long) requestHeaders.getAttribute("userId");
            
            // 결제 정보 생성
            Map<String, Object> result = paymentService.createPaymentOrder(request, userId);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("결제 정보 생성 중 오류", e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Auth
    @PostMapping("/complete")
    @ApiOperation(value = "결제 완료", notes = "포트원 결제 완료 후 검증 및 펀딩 가입 처리를 합니다.")
    public ResponseEntity<Map<String, Object>> completePayment(@RequestBody PaymentCompleteRequestDTO request,
                                                               HttpServletRequest requestHeaders) {
        log.info("===== 결제 완료 API 호출됨 =====");
        try {
            log.info("결제 완료 요청 - imp_uid: {}, merchant_uid: {}", 
                request.getImpUid(), request.getMerchantUid());
            
            // 1. 결제 완료 처리
            Map<String, Object> result = paymentService.completePayment(request);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "결제 처리 중 오류가 발생했습니다."));
        }
    }
}