package org.funding.retryVotes.service;

import lombok.RequiredArgsConstructor;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.retryVotes.dao.RetryVotesDAO;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.vo.RetryVotesVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetryVotesService {

    private final RetryVotesDAO retryVotesDAO;
    private final FundDAO fundDAO;

    // 투표 등록
    public String doVote(DoVoteRequestDTO voteRequestDTO) {
        boolean alreadyVoted = retryVotesDAO.existsVoteByUserIdAndFundingId(voteRequestDTO.getUserId(), voteRequestDTO.getFundId());
        // 중복 검사
        if (alreadyVoted) {
            throw new IllegalStateException("이미 투표하신 펀딩입니다.");
        }

        FundVO fund = fundDAO.selectById(voteRequestDTO.getFundId());

        // 펀딩이 종료됐는지 검사
        if (fund.getProgress() != ProgressType.End) {
            throw new IllegalStateException("아직 펀딩이 종료되지 않았습니다.");
        }

        RetryVotesVO retryVotesVO = new RetryVotesVO();
        retryVotesVO.setUserId(voteRequestDTO.getUserId());
        retryVotesVO.setFundingId(voteRequestDTO.getFundId());
        retryVotesDAO.addRetryVotes(retryVotesVO);

        // 펀딩에 투표 수 추가
        int count = fund.getRetryVotesCount();
        if (count > 0) {
            fund.setRetryVotesCount(fund.getRetryVotesCount() + 1);
        }

        return "투표가 정상적으로 등록되었습니다.";
    }

    // 투표 취소
    public String deleteVote(DoVoteRequestDTO voteRequestDTO) {
        boolean alreadyVoted = retryVotesDAO.existsVoteByUserIdAndFundingId(voteRequestDTO.getUserId(), voteRequestDTO.getFundId());
        // 투표 취소방지
        if (!alreadyVoted) {
            throw new IllegalStateException("투표를 안하셨기에 투표를 취소 할 수 없습니다.");
        }

        FundVO fund = fundDAO.selectById(voteRequestDTO.getFundId());

        // 펀딩이 종료됐는지 검사
        if (fund.getProgress() != ProgressType.End) {
            throw new IllegalStateException("아직 펀딩이 종료되지 않았습니다.");
        }
        retryVotesDAO.deleteRetryVotes(voteRequestDTO);

        // 펀딩에 투표 수 취소 반영
        int count = fund.getRetryVotesCount();
        if (count > 0) {
            fund.setRetryVotesCount(fund.getRetryVotesCount() - 1);
        }
        return "투표가 정상적으로 취소되었습니다";
    }

}
