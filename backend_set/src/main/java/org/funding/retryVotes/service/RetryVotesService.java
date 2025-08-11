package org.funding.retryVotes.service;

import lombok.RequiredArgsConstructor;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.RetryVotesException;
import org.funding.retryVotes.dao.RetryVotesDAO;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.dto.MyVotedFundDTO;
import org.funding.retryVotes.vo.RetryVotesVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetryVotesService {

    private final RetryVotesDAO retryVotesDAO;
    private final FundDAO fundDAO;

    // 투표 등록
    public String doVote(DoVoteRequestDTO voteRequestDTO, Long userId) {
        boolean alreadyVoted = retryVotesDAO.existsVoteByUserIdAndFundingId(userId, voteRequestDTO.getFundId());
        // 중복 검사
        if (alreadyVoted) {
            throw new RetryVotesException(ErrorCode.ALREADY_RETRY_VOTE);
        }

        FundVO fund = fundDAO.selectById(voteRequestDTO.getFundId());

        // 펀딩이 종료됐는지 검사
        if (fund.getProgress() != ProgressType.End) {
            throw new RetryVotesException(ErrorCode.NOT_END_FUND);
        }

        RetryVotesVO retryVotesVO = new RetryVotesVO();
        retryVotesVO.setUserId(userId);
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
    public String deleteVote(DoVoteRequestDTO voteRequestDTO, Long userId) {
        boolean alreadyVoted = retryVotesDAO.existsVoteByUserIdAndFundingId(userId, voteRequestDTO.getFundId());
        // 투표 취소방지
        if (!alreadyVoted) {
            throw new RetryVotesException(ErrorCode.NOT_CANCEL_RETRY_VOTE);
        }

        FundVO fund = fundDAO.selectById(voteRequestDTO.getFundId());

        // 펀딩이 종료됐는지 검사
        if (fund.getProgress() != ProgressType.End) {
            throw new RetryVotesException(ErrorCode.NOT_END_FUND);
        }
        retryVotesDAO.deleteRetryVotes(voteRequestDTO);

        // 펀딩에 투표 수 취소 반영
        int count = fund.getRetryVotesCount();
        if (count > 0) {
            fund.setRetryVotesCount(fund.getRetryVotesCount() - 1);
        }
        return "투표가 정상적으로 취소되었습니다";
    }

    // 내가 투표한 펀딩 전체 조회
    public List<MyVotedFundDTO> findMyVotedFunds(Long userId) {
        return retryVotesDAO.findVotedFundDetailsByUserId(userId);
    }

}
