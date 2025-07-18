package org.funding.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoanProjectDTO extends ProjectDTO {
    private Long loanLimit;
    private BigDecimal desiredInterestRate;
    private String reward;
    private String rewardCondition;
}
