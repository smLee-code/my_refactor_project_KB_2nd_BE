package org.funding.votes.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VotesDAO {
    // 해당 유저가 몇번의 투표를 했는지
    int countVotesByUserId(Long userId);
}
