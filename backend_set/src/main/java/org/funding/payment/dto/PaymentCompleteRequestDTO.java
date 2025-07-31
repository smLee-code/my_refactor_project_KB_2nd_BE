package org.funding.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteRequestDTO {
    @JsonProperty("imp_uid")
    private String impUid;           // 포트원 결제 고유번호
    
    @JsonProperty("merchant_uid")
    private String merchantUid;      // 가맹점 주문번호
}