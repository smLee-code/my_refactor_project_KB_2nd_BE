package org.funding.retryVotes.service;

import lombok.RequiredArgsConstructor;
import org.funding.retryVotes.dao.RetryVotesDAO;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.vo.RetryVotesVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetryVotesService {

    private final RetryVotesDAO retryVotesDAO;

    // 투표 등록
    public String doVote(DoVoteRequestDTO voteRequestDTO) {
        boolean alreadyVoted = retryVotesDAO.existsVoteByUserIdAndFundingId(voteRequestDTO.getUserId(), voteRequestDTO.getFundId());
        // 중복 검사
        if (alreadyVoted) {
            throw new IllegalStateException("이미 투표하신 펀딩입니다.");
        }

        RetryVotesVO retryVotesVO = new RetryVotesVO();
        retryVotesVO.setUserId(voteRequestDTO.getUserId());
        retryVotesVO.setFundingId(voteRequestDTO.getFundId());
        retryVotesDAO.addRetryVotes(retryVotesVO);

        return "투표가 정상적으로 등록되었습니다.";
    }

    // 투표 취소
    public String deleteVote(DoVoteRequestDTO voteRequestDTO) {
        retryVotesDAO.deleteRetryVotes(voteRequestDTO);
        return "투표가 정상적으로 취소되었습니다";
    }

}
