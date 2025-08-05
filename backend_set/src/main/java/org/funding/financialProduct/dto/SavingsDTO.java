package org.funding.financialProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SavingsDTO extends FinancialProductDTO {
    private Integer periodDays;
    private Double interestRate;
    private String successCondition;
}
