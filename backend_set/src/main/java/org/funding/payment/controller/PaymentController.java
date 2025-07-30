package org.funding.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.payment.dto.PaymentCompleteRequestDTO;
import org.funding.payment.dto.PaymentCreateRequestDTO;
import org.springframework.security.core.Authentication;
import org.funding.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody PaymentCreateRequestDTO request,
            Authentication authentication) {
        try {
            log.info("결제 정보 생성 요청 - fundId: {}", 
                request.getFundId());
            
            // 사용자 ID 추출
            Long userId = paymentService.getUserIdFromAuthentication(authentication);
            
            // 결제 정보 생성
            Map<String, Object> result = paymentService.createPaymentOrder(request, userId);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("결제 정보 생성 중 오류", e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completePayment(@RequestBody PaymentCompleteRequestDTO request) {
        try {
            log.info("결제 완료 요청 - imp_uid: {}", request.getImpUid());
            
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