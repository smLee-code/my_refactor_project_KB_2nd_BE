package org.funding.retryVotes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.fund.vo.FundVO;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.RetryVotesException;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.dto.MyVotedFundDTO;
import org.funding.retryVotes.service.RetryVotesService;
import org.funding.security.util.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/retryVotes")
public class RetryVotesController {

    private final RetryVotesService retryVotesService;


    // 투표
    @Auth
    @PostMapping("/do")
    public ResponseEntity<String> doVote(@RequestBody DoVoteRequestDTO voteRequestDTO,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(retryVotesService.doVote(voteRequestDTO, userId));
    }

    // 투표 취소 api
    @Auth
    @DeleteMapping("/cancel")
    public ResponseEntity<String> deleteVote(@RequestBody DoVoteRequestDTO voteRequestDTO,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(retryVotesService.deleteVote(voteRequestDTO, userId));
    }

    // 내가 투표한 펀딩 전체 조회
    @Auth
    @GetMapping("/my-fund/list")
    public ResponseEntity<?> getMyAllVotedFunds(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new RetryVotesException(ErrorCode.MEMBER_NOT_FOUND);
        }

        List<MyVotedFundDTO> myVotedFunds = retryVotesService.findMyVotedFunds(userId);

        return ResponseEntity.ok(myVotedFunds);
    }


}
