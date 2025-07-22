package org.funding.votes.controller;

import lombok.RequiredArgsConstructor;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VotesController {

    private final VotesService votesService;

    @PostMapping("")
    public ResponseEntity<VotesResponseDTO> votesForProject(@RequestBody VotesRequestDTO requestDTO) {
        VotesResponseDTO responseDTO = votesService.createVotes(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("")
    public void cancelVotingOnProject(@RequestBody VotesRequestDTO requestDTO) {
        votesService.deleteVotes(requestDTO);
    }

}