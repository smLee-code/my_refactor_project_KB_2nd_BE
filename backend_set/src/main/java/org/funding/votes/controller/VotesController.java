package org.funding.votes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VotesController {

    private final VotesService votesService;

    @PostMapping("")
    public ResponseEntity<VotesResponseDTO> castVote(@RequestBody VotesRequestDTO requestDTO) {
        VotesResponseDTO responseDTO = votesService.createVotes(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> cancelVote(@RequestBody VotesRequestDTO requestDTO) {
        votesService.deleteVotes(requestDTO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-votes")
    public ResponseEntity<List<Long>> findVotedProjects(@RequestBody Long userId) {
        List<Long> votedProjects = votesService.findVotedProjects(userId);

        return ResponseEntity.ok(votedProjects);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countVotes(@RequestParam("projectId") Long projectId) {
        Long voteCount = votesService.countVotes(projectId);

        return ResponseEntity.ok(voteCount);
    }

}