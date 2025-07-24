package org.funding.project.vo;

import lombok.*;

import java.math.BigDecimal;


import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProjectVO {

    private Long projectId;

    // Loan 고유 칼럼
    private Long loanLimit; // 대출 한도
    private BigDecimal desiredInterestRate; // 희망 금리
    private String reward; // 리워드
    private String rewardCondition; // 리워드 조건
}
