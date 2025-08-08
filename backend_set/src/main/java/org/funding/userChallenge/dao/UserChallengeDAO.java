package org.funding.userChallenge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.userChallenge.dto.UserChallengeDetailDTO;
import org.funding.userChallenge.vo.UserChallengeVO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserChallengeDAO {

    void insertUserChallenge(UserChallengeVO challengeVO);

    void updateUserChallengeSuccess(@Param("userChallengeId") Long id);

    void updateUserChallengeFail(@Param("userChallengeId") Long id);

    void updateChallengeStatus(@Param("userChallengeId") Long id, @Param("status") String status);

    // 유저가 첼린지에 가입되어있는지 여부
    boolean existsByIdAndUserId(@Param("userChallengeId") Long userChallengeId, @Param("userId") Long userId);

    UserChallengeVO findById(@Param("userChallengeId") Long id);

    // 유저 챌린지 참여 취소
    void deleteUserChallenge(@Param("userChallengeId") Long id);

    List<UserChallengeDetailDTO> findAllChallengesByUserId(@Param("userId") Long userId);

}
