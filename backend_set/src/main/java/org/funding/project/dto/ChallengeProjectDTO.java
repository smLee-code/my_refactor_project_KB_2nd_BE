package org.funding.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChallengeProjectDTO extends ProjectDTO {
    private Long challengePeriodDays;
    private String reward;
    private String rewardCondition;
}
