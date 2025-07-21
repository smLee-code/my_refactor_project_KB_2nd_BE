package org.funding.project.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
public class DonationProjectResponseDTO extends ProjectResponseDTO {
    // Donation 고유 칼럼
    private String recipient; // 기부처
    private String usagePlan; // 기부금 사용 계획
    private Long targetAmount; // 목표 모금액
}
