package org.funding.votes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.exception.DuplicateVoteException;
import org.funding.security.util.Auth;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Auth
    @GetMapping("")
    public ResponseEntity<Boolean> hasVoted(@RequestParam("projectId") Long projectId,
                                            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        VotesRequestDTO requestDTO = new VotesRequestDTO();
        requestDTO.setProjectId(projectId);
        requestDTO.setUserId(userId);

        Boolean voted = votesService.hasVoted(requestDTO, userId);

        return ResponseEntity.ok(voted);
    }

    @Auth
    @DeleteMapping("")
    public ResponseEntity<Void> cancelVote(@RequestBody Long projectId,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        votesService.deleteVotes(projectId, userId);

        return ResponseEntity.noContent().build();
    }

    @Auth
    @GetMapping("/my-votes")
    public ResponseEntity<List<Long>> findVotedProjects(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Long> votedProjects = votesService.findVotedProjects(userId);

        return ResponseEntity.ok(votedProjects);
    }

    @Auth
    @GetMapping("/count")
    public ResponseEntity<Long> countVotes(@RequestParam("projectId") Long projectId,
                                           HttpServletRequest request) {

        Long voteCount = votesService.countVotes(projectId);

        return ResponseEntity.ok(voteCount);
    }

    @Auth
    @PostMapping("")
    public ResponseEntity<VotesResponseDTO> toggleVote(@RequestBody Long projectId,
                                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        VotesResponseDTO responseDTO = votesService.toggleVote(projectId, userId);

        if (responseDTO == null) {
            // 삭제된 경우
            return ResponseEntity.noContent().build();
        }

        // 추가된 경우
        return ResponseEntity.ok(responseDTO);
    }


}