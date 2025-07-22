package org.funding.votes.dao;

import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.vo.VotesVO;

public interface VotesDAO {

    public VotesVO selectVotes(VotesRequestDTO dto);

    public VotesVO selectVotesById(Long voteId);

    public void insertVotes(VotesVO vo);

    public void deleteVotes(Long voteId);
}
