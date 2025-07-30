package org.funding.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteRequestDTO {
    private String impUid;           // 포트원 결제 고유번호
    private String merchantUid;      // 가맹점 주문번호
}