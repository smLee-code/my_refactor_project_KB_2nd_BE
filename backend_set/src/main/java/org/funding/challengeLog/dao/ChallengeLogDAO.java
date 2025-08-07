package org.funding.challengeLog.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.challengeLog.vo.ChallengeLogVO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ChallengeLogDAO {

    void insertChallengeLog(ChallengeLogVO log);

    ChallengeLogVO selectLogByUserAndDate(@Param("userChallengeId") Long id, @Param("logDate") LocalDate date);

    List<LocalDate> selectAllLogDatesByUserChallengeId(Long userChallengeId);

    // 중복 방지
    boolean existsByUserChallengeIdAndLogDate(Long userChallengeId, LocalDate logDate);


}
