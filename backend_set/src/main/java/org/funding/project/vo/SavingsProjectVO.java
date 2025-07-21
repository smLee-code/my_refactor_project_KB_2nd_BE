package org.funding.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
public class SavingsProjectVO{
    
    // Savings 고유 칼럼
    private Long periodDays; // 상품기간
    private BigDecimal interestRate; // 연이율 (%)
    private String successCondition; // 목표 달성 조건
}
