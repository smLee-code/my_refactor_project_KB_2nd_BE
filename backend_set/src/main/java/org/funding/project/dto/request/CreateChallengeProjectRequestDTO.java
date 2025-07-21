package org.funding.project.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.funding.project.vo.ChallengeProjectVO;
import org.funding.project.vo.ProjectVO;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateChallengeProjectRequestDTO extends CreateProjectRequestDTO {

    // Challenge 고유 칼럼
    private Long challengePeriodDays; // 챌린지 기간
    private String reward; // 리워드
    private String rewardCondition; // 리워드 조건

    public ChallengeProjectVO toChallengeVO() {
        return ChallengeProjectVO.builder()
                .challengePeriodDays(this.getChallengePeriodDays())
                .reward(this.getReward())
                .rewardCondition(this.getRewardCondition())
                .build();
    }
}
