package org.funding.financialProduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DonationVO {
    private Long donationId; // 기부 상품 id
    private Long productId; // 상품 ID
    private String recipient; // 기부처
    private String usagePlan; // 사용 계획
    private Integer minDonationAmount; // 최소 기부 금액
    private Integer maxDonationAmount; // 최대 기부 금액 (nullable)
    private Long targetAmount; // 목표 모금액
}
