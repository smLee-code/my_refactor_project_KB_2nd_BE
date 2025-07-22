package org.funding.retryVotes.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.vo.RetryVotesVO;

@Mapper
public interface RetryVotesDAO {

    // 투표 추가
    int addRetryVotes(RetryVotesVO retryVotesVO);

    // 특정 펀딩에 대한 투표 수 조회
    int countRetryVotesByFundingId(Long fundingId);

    // 중복 투표 방지용
    boolean existsVoteByUserIdAndFundingId(@Param("userId") Long userId, @Param("fundingId") Long fundingId);

    // 투표 취소
    void deleteRetryVotes(DoVoteRequestDTO voteRequestDTO);
}
