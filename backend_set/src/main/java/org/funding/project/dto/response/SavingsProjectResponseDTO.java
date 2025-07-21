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
public class SavingsProjectResponseDTO extends ProjectResponseDTO {
    // Savings 고유 칼럼
    private Long periodDays; // 상품기간
    private BigDecimal interestRate; // 연이율 (%)
    private String successCondition; // 목표 달성 조건
}
