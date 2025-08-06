package org.funding.retryVotes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.service.RetryVotesService;
import org.funding.security.util.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/retryVotes")
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


}
