package org.funding.financialProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class LoanDTO extends FinancialProductDTO {
    private Long loanLimit;
    private LocalDateTime repaymentStartDate;
    private LocalDateTime repaymentEndDate;
    private Double minInterestRate;
    private Double maxInterestRate;
    private String reward;
    private String rewardCondition;
}
