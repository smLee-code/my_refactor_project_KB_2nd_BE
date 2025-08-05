package org.funding.challengeLog.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.challengeLog.vo.ChallengeLogVO;

import java.time.LocalDate;

@Mapper
public interface ChallengeLogDAO {

    void insertChallengeLog(ChallengeLogVO log);

    ChallengeLogVO selectLogByUserAndDate(@Param("userChallengeId") Long id, @Param("logDate") LocalDate date);
}
