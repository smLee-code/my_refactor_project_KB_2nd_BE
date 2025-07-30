package org.funding.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequestDTO {
    private Long fundId;         // 펀딩 ID
    private Integer amount;      // 결제 금액 (기부형일 때만 사용)
}