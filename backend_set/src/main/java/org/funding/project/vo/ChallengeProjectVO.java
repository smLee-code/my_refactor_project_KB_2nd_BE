package org.funding.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChallengeProjectVO extends ProjectVO {
    private Long challengePeriodDays;
    private String reward;
    private String rewardCondition;
}
