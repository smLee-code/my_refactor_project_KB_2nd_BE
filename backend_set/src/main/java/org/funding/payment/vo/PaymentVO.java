package org.funding.payment.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVO {
    private Long paymentId;
    private String merchantUid;      // 가맹점 주문번호
    private String impUid;           // 포트원 결제 고유번호
    private Long userId;             // 사용자 ID
    private Long fundId;             // 펀딩 ID
    private String fundingType;      // 펀딩 타입 (donation, challenge)
    private Integer amount;          // 결제 금액
    private String status;           // 결제 상태 (PENDING, PAID, FAILED, CANCELLED)
    private String payMethod;        // 결제 수단
    private String pgProvider;       // PG사
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime paidAt;    // 결제일시
    private LocalDateTime updatedAt; // 수정일시
}