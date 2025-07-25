package org.funding.userChallenge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.userChallenge.vo.UserChallengeVO;

import java.time.LocalDate;

@Mapper
public interface UserChallengeDAO {

    void insertUserChallenge(UserChallengeVO challengeVO);

    void updateUserChallengeSuccess(@Param("userChallengeId") Long id);

    void updateUserChallengeFail(@Param("userChallengeId") Long id);

    void updateChallengeStatus(@Param("userChallengeId") Long id, @Param("status") String status);
}
