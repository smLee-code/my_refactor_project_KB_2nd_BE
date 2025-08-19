package org.funding.votes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.security.util.Auth;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "프로젝트 투표(좋아요) API")
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VotesController {

    private final VotesService votesService;

    @ApiOperation(value = "사용자의 투표 여부 확인", notes = "현재 로그인한 사용자가 특정 프로젝트에 투표했는지 여부를 확인합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<Boolean> hasVoted(
            @ApiParam(value = "확인할 프로젝트 ID", required = true, example = "1") @PathVariable Long projectId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        VotesRequestDTO requestDTO = new VotesRequestDTO();
        requestDTO.setProjectId(projectId);
        requestDTO.setUserId(userId);
        Boolean voted = votesService.hasVoted(requestDTO);
        return ResponseEntity.ok(voted);
    }

    @ApiOperation(value = "프로젝트 투표 취소", notes = "특정 프로젝트에 대한 투표를 취소합니다.")
    @Auth
    @DeleteMapping("")
    public ResponseEntity<Void> cancelVote(
            @ApiParam(value = "투표를 취소할 프로젝트 ID", required = true) @RequestBody Long projectId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        votesService.deleteVotes(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "내가 투표한 프로젝트 목록 조회", notes = "현재 로그인한 사용자가 투표한 모든 프로젝트의 ID 목록을 조회합니다.")
    @Auth
    @GetMapping("/my-votes")
    public ResponseEntity<List<Long>> findVotedProjects(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Long> votedProjects = votesService.findVotedProjects(userId);
        return ResponseEntity.ok(votedProjects);
    }

    @ApiOperation(value = "프로젝트 투표 수 조회", notes = "특정 프로젝트가 받은 총 투표(좋아요) 수를 조회합니다.")
    @GetMapping("/{projectId}/count")
    public ResponseEntity<Long> countVotes(
            @ApiParam(value = "투표 수를 조회할 프로젝트 ID", required = true, example = "1") @PathVariable Long projectId) {
        Long voteCount = votesService.countVotes(projectId);
        return ResponseEntity.ok(voteCount);
    }

    @ApiOperation(value = "프로젝트 투표/투표 취소 (토글)", notes = "특정 프로젝트에 대해 투표합니다. 이미 투표한 상태라면 투표가 취소됩니다 (토글 방식).")
    @Auth
    @PostMapping("/{projectId}")
    public ResponseEntity<VotesResponseDTO> toggleVote(
            @ApiParam(value = "투표할 프로젝트 ID", required = true, example = "1") @PathVariable Long projectId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        VotesResponseDTO responseDTO = votesService.toggleVote(projectId, userId);
        if (responseDTO == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responseDTO);
    }
}