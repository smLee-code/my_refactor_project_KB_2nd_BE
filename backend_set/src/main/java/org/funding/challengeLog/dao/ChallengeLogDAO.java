package org.funding.challengeLog.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.challengeLog.vo.ChallengeLogVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ChallengeLogDAO {

    void insertChallengeLog(ChallengeLogVO log);


    ChallengeLogVO selectLogByUserAndDate(@Param("userChallengeId") Long id, @Param("logDate") LocalDate date);

    List<LocalDate> selectAllLogDatesByUserChallengeId(Long userChallengeId);

    // 중복 방지
    boolean existsByUserChallengeIdAndLogDate(@Param("userChallengeId") Long userChallengeId, @Param("logDate") LocalDate logDate);

    // 챌린지 참여자 조회
    List<ChallengeLogVO> selectAllLogsByUserChallengeId(Long userChallengeId);

    // 챌린지 참여자 기록 조회
    List<ChallengeLogVO> findLogsByUserChallengeId(Map<String, Object> params);

    // 수동 챌린지 인증
    ChallengeLogVO selectLogById(Long logId);
    void updateChallengeLog(ChallengeLogVO log);


}
