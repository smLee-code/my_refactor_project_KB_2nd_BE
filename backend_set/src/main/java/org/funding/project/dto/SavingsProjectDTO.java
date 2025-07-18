package org.funding.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class SavingsProjectDTO extends ProjectDTO {
    private Long periodDays;
    private BigDecimal interestRate;
    private String successCondition;
}
