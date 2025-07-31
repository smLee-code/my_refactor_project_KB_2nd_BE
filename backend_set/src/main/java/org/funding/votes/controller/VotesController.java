package org.funding.votes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.exception.DuplicateVoteException;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VotesController {

    private final VotesService votesService;


    //맨 아래 toggleLike 랑 mapping 겹쳐서 일단 주석처리
//    @PostMapping("")
//    public ResponseEntity<VotesResponseDTO> castVote(@RequestBody VotesRequestDTO requestDTO) {
//
//        try {
//            VotesResponseDTO responseDTO = votesService.toggleVote(requestDTO);
//            return ResponseEntity.ok(responseDTO);
//        } catch (DuplicateVoteException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//        }
//
//    }

    @GetMapping("")
    public ResponseEntity<Boolean> hasVoted(@RequestParam("userId") Long userId, @RequestParam("projectId") Long projectId) {

        VotesRequestDTO requestDTO = new VotesRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setProjectId(projectId);

        Boolean voted = votesService.hasVoted(requestDTO);

        return ResponseEntity.ok(voted);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> cancelVote(@RequestBody VotesRequestDTO requestDTO) {
        votesService.deleteVotes(requestDTO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-votes")
    public ResponseEntity<List<Long>> findVotedProjects(@RequestParam("userId") Long userId) {
        List<Long> votedProjects = votesService.findVotedProjects(userId);

        return ResponseEntity.ok(votedProjects);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countVotes(@RequestParam("projectId") Long projectId) {
        Long voteCount = votesService.countVotes(projectId);

        return ResponseEntity.ok(voteCount);
    }

    @PostMapping("")
    public ResponseEntity<VotesResponseDTO> toggleVote(@RequestBody VotesRequestDTO requestDTO) {
        VotesResponseDTO responseDTO = votesService.toggleVote(requestDTO);

        if (responseDTO == null) {
            // 삭제된 경우
            return ResponseEntity.noContent().build();
        }

        // 추가된 경우
        return ResponseEntity.ok(responseDTO);
    }


}