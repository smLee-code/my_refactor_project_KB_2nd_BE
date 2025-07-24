package org.funding.project.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.funding.project.vo.DonationProjectVO;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateDonationProjectRequestDTO extends CreateProjectRequestDTO {

    // Donation 고유 칼럼
    private String recipient; // 기부처
    private String usagePlan; // 기부금 사용 계획
    private Long targetAmount; // 목표 모금액

    public DonationProjectVO toDonationVO() {
        return DonationProjectVO.builder()
                .recipient(this.getRecipient())
                .usagePlan(this.getUsagePlan())
                .targetAmount(this.getTargetAmount())
                .build();
    }
}
