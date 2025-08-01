package org.funding.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCreateRequestDTO {
    private Long fundId;         // 펀딩 ID
    private Integer amount;      // 결제 금액 (기부형일 때만 사용)
    private Map<String, Object> metadata;  // 추가 정보 (익명여부 등)
}