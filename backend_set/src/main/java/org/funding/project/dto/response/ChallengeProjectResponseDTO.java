package org.funding.project.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChallengeProjectResponseDTO extends ProjectResponseDTO {
    // Challenge 고유 칼럼
    private Long challengePeriodDays; // 챌린지 기간
    private String reward; // 리워드
    private String rewardCondition; // 리워드 조건
}
