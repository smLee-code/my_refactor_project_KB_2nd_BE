package org.funding.retryVotes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@Api(tags = "재출시 투표 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/retryVotes")
public class RetryVotesController {

    private final RetryVotesService retryVotesService;

    @ApiOperation(value = "재출시 투표하기", notes = "종료된 펀딩에 대해 재출시를 요청하는 투표를 합니다.")
    @Auth
    @PostMapping("/do")
    public ResponseEntity<String> doVote(@RequestBody DoVoteRequestDTO voteRequestDTO,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(retryVotesService.doVote(voteRequestDTO, userId));
    }

    @ApiOperation(value = "재출시 투표 취소", notes = "재출시 투표를 취소합니다.")
    @Auth
    @DeleteMapping("/cancel")
    public ResponseEntity<String> deleteVote(@RequestBody DoVoteRequestDTO voteRequestDTO,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(retryVotesService.deleteVote(voteRequestDTO, userId));
    }

    @ApiOperation(value = "내가 투표한 펀딩 목록 조회", notes = "현재 로그인한 사용자가 재출시 투표를 한 모든 펀딩 목록을 조회합니다.")
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
