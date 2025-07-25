package org.funding.userChallenge.vo;

import lombok.Data;
import org.funding.userChallenge.vo.enumType.ChallengeStatus;

@Data
public class UserChallengeVO {
    private Long userChallengeId;
    private Long fundId;
    private Long userId;
    private int currentCount;
    private int failCount;
    private ChallengeStatus challengeStatus; // 진행 상태
}
