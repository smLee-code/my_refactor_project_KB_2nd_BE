package org.funding.financialProduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoanVO {
    private Long installmentId; // 대출 id
    private Long productId; // 상품 id
    private Long loanLimit; // 대출 한도 (BIGINT)
    private LocalDateTime repaymentStartDate; // 상환 시작일
    private LocalDateTime repaymentEndDate; // 상환 마감일
    private Double minInterestRate; // 최저 금리 (DECIMAL(5,2))
    private Double maxInterestRate; // 최고 금리 (DECIMAL(5,2))
    private String reward; // 리워드 (nullable)
    private String rewardCondition; // 리워드 조건 (nullable)
}
