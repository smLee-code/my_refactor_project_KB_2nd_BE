package org.funding.userChallenge.dto;

import lombok.Data;
import org.funding.challengeLog.vo.ChallengeLogVO;

import java.util.List;

@Data
public class ChallengeDetailResponseDTO {

    private UserChallengeDetailDTO challengeInfo;
    private List<ChallengeLogVO> dailyLogs;
}
