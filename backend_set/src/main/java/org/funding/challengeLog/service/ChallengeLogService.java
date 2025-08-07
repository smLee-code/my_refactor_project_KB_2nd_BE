package org.funding.challengeLog.service;

import lombok.RequiredArgsConstructor;
import org.funding.challengeLog.dao.ChallengeLogDAO;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeLogService {

    private final ChallengeLogDAO challengeLogDAO;

    // 전체 로그 조회
    public List<ChallengeLogVO> getAllLogsByUserChallenge(Long userChallengeId) {
        return challengeLogDAO.selectAllLogsByUserChallengeId(userChallengeId);
    }

}
