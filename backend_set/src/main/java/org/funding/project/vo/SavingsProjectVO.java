package org.funding.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class SavingsProjectVO extends ProjectVO {
    private Long periodDays;
    private BigDecimal interestRate;
    private String successCondition;
}
