package org.funding.project.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.SavingsProjectVO;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateSavingsProjectRequestDTO extends CreateProjectRequestDTO {

    // Savings 고유 칼럼
    private Long periodDays; // 상품기간
    private BigDecimal interestRate; // 연이율 (%)
    private String successCondition; // 목표 달성 조건

    public SavingsProjectVO toSavingsVO() {
        return SavingsProjectVO.builder()
                .periodDays(this.getPeriodDays())
                .interestRate(this.getInterestRate())
                .successCondition(this.getSuccessCondition())
                .build();
    }
}
