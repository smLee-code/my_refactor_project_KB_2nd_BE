package org.funding.financialProduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SavingsVO {
    private Long savingsId; // 저축 id
    private Long productId; // 상품 id
    private Integer periodDays; // 상품기간
    private Double interestRate;  // 연이율 (%)
    private String successCondition; // 목표 달성 조건 (nullable)

}
