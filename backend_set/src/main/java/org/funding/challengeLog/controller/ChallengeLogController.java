package org.funding.challengeLog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.challengeLog.service.ChallengeLogService;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.security.util.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "챌린지 기록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge-logs")
public class ChallengeLogController {

    private final ChallengeLogService challengeLogService;

    @ApiOperation(value = "특정 챌린지 참여의 모든 기록 조회", notes = "사용자가 참여한 특정 챌린지(userChallengeId)에 대한 모든 인증 기록을 조회합니다.")
    @Auth
    @GetMapping("/{userChallengeId}/all")
    public ResponseEntity<List<ChallengeLogVO>> getAllLogsByUserChallenge(
            @ApiParam(value = "사용자 챌린지 ID", required = true, example = "1") @PathVariable Long userChallengeId,
            HttpServletRequest request) {

        List<ChallengeLogVO> logs = challengeLogService.getAllLogsByUserChallenge(userChallengeId);
        return ResponseEntity.ok(logs);
    }
}