package org.funding.userChallenge.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.security.util.Auth;
import org.funding.userChallenge.dto.ChallengeParticipantDTO;
import org.funding.userChallenge.service.UserChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "챌린지 관리 API (관리자용)")
@RestController
@RequestMapping("/api/admin/challenge")
@RequiredArgsConstructor
public class ChallengeAdminController {

    private final UserChallengeService userChallengeService;

    @ApiOperation(value = "챌린지 참여자 목록 조회", notes = "특정 챌린지(fundId)에 참여한 모든 사용자 목록을 조회합니다. 챌린지 생성자만 가능합니다.")
    @Auth
    @GetMapping("/{fundId}/participants")
    public ResponseEntity<List<ChallengeParticipantDTO>> getChallengeParticipants(
            @ApiParam(value = "펀딩 ID", required = true, example = "1") @PathVariable("fundId") Long fundId,
            HttpServletRequest request) {
        Long creatorId = (Long) request.getAttribute("userId");
        List<ChallengeParticipantDTO> participants = userChallengeService.getChallengeParticipants(fundId, creatorId);
        return ResponseEntity.ok(participants);
    }

    @ApiOperation(value = "특정 참여자의 인증 기록 조회", notes = "특정 참여자(userChallengeId)의 모든 인증 기록을 조회합니다. 상태별 필터링이 가능합니다.")
    @Auth
    @GetMapping("/logs/{userChallengeId}")
    public ResponseEntity<List<ChallengeLogVO>> getParticipantLogs(
            @ApiParam(value = "사용자 챌린지 ID", required = true, example = "1") @PathVariable Long userChallengeId,
            @ApiParam(value = "조회할 인증 상태 (선택)", example = "HumanVerify") @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long creatorId = (Long) request.getAttribute("userId");
        List<ChallengeLogVO> logs = userChallengeService.getParticipantLogs(userChallengeId, status, creatorId);
        return ResponseEntity.ok(logs);
    }

    @ApiOperation(value = "인증 기록 수동 승인", notes = "'수동 검증 필요(HumanVerify)' 상태의 인증 기록을 '승인(Verified)' 처리합니다. 챌린지 생성자만 가능합니다.")
    @Auth
    @PatchMapping("/logs/{logId}/approve")
    public ResponseEntity<String> manuallyApproveLog(
            @ApiParam(value = "인증 기록 ID", required = true, example = "1") @PathVariable Long logId,
            HttpServletRequest request) {
        Long creatorId = (Long) request.getAttribute("userId");
        userChallengeService.manuallyVerifyLog(logId, creatorId, true);
        return ResponseEntity.ok("수동 승인 처리 완료");
    }

    @ApiOperation(value = "인증 기록 수동 반려", notes = "'수동 검증 필요(HumanVerify)' 상태의 인증 기록을 '반려(UnVerified)' 처리합니다. 챌린지 생성자만 가능합니다.")
    @Auth
    @PatchMapping("/logs/{logId}/reject")
    public ResponseEntity<String> manuallyRejectLog(
            @ApiParam(value = "인증 기록 ID", required = true, example = "1") @PathVariable Long logId,
            HttpServletRequest request) {
        Long creatorId = (Long) request.getAttribute("userId");
        userChallengeService.manuallyVerifyLog(logId, creatorId, false); // false for reject
        return ResponseEntity.ok("수동 반려 처리 완료");
    }
}