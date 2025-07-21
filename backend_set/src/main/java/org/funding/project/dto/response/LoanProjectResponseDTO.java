package org.funding.project.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoanProjectResponseDTO extends ProjectResponseDTO {

    // Loan 고유 칼럼
    private Long loanLimit; // 대출 한도
    private BigDecimal desiredInterestRate; // 희망 금리
    private String reward; // 리워드
    private String rewardCondition; // 리워드 조건

}
