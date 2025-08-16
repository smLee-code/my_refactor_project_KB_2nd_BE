package org.funding.userChallenge.controller;

import lombok.RequiredArgsConstructor;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.security.util.Auth;
import org.funding.userChallenge.dto.ChallengeParticipantDTO;
import org.funding.userChallenge.service.UserChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin/challenge")
@RequiredArgsConstructor
public class ChallengeAdminController {

    private final UserChallengeService userChallengeService;

    // 챌린지 참여자 목록 조회
    @Auth
    @GetMapping("/{fundId}/participants")
    public ResponseEntity<List<ChallengeParticipantDTO>> getChallengeParticipants(@PathVariable("fundId") Long fundId,
                                                                                  HttpServletRequest request) {
        Long creatorId = (Long) request.getAttribute("userId");
        List<ChallengeParticipantDTO> participants = userChallengeService.getChallengeParticipants(fundId, creatorId);
        return ResponseEntity.ok(participants);
    }

    // 특정 참여자 챌린지 기록 조회
    @Auth
    @GetMapping("/logs/{userChallengeId}")
    public ResponseEntity<List<ChallengeLogVO>> getParticipantLogs(
            @PathVariable Long userChallengeId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long creatorId = (Long) request.getAttribute("userId");
        List<ChallengeLogVO> logs = userChallengeService.getParticipantLogs(userChallengeId, status, creatorId);
        return ResponseEntity.ok(logs);
    }

    //
}
