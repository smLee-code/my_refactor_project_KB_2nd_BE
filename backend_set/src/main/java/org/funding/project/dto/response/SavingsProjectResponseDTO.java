package org.funding.project.dto.response;

import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SavingsProjectResponseDTO{

    // Savings 고유 칼럼
    private Long periodDays; // 상품기간
    private BigDecimal interestRate; // 연이율 (%)
    private String successCondition; // 목표 달성 조건
}
