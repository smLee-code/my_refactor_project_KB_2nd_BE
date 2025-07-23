package org.funding.retryVotes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.retryVotes.dto.DoVoteRequestDTO;
import org.funding.retryVotes.service.RetryVotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/retryVotes")
public class RetryVotesController {

    private final RetryVotesService retryVotesService;


    // 투표
    @PostMapping("/do")
    public ResponseEntity<String> doVote(@RequestBody DoVoteRequestDTO voteRequestDTO) {
        return ResponseEntity.ok(retryVotesService.doVote(voteRequestDTO));
    }

    // 투표 취소 api
    @DeleteMapping("/cancel")
    public ResponseEntity<String> deleteVote(@RequestBody DoVoteRequestDTO voteRequestDTO) {
        return ResponseEntity.ok(retryVotesService.deleteVote(voteRequestDTO));
    }


}
