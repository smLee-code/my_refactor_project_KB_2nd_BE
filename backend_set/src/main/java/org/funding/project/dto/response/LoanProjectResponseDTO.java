package org.funding.project.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProjectResponseDTO extends ProjectResponseDTO {
    // Loan 고유 칼럼
    private Long loanLimit; // 대출 한도
    private BigDecimal desiredInterestRate; // 희망 금리
    private String reward; // 리워드
    private String rewardCondition; // 리워드 조건
}
