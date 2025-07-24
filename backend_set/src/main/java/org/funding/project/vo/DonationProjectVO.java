package org.funding.project.vo;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationProjectVO {

    private Long projectId;

    // Donation 고유 칼럼
    private String recipient; // 기부처
    private String usagePlan; // 기부금 사용 계획
    private Long targetAmount; // 목표 모금액
}
