package org.funding.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DonationProjectVO extends ProjectVO {

    // Donation 고유 칼럼
    private String recipient; // 기부처
    private String usagePlan; // 기부금 사용 계획
    private Long targetAmount; // 목표 모금액
}
