package org.funding.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoanProjectVO extends ProjectVO {
    private Long loanLimit;
    private BigDecimal desiredInterestRate;
    private String reward;
    private String rewardCondition;
}
