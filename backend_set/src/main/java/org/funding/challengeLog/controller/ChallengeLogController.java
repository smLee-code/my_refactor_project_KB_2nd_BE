package org.funding.challengeLog.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge-logs")
public class ChallengeLogController {

    private final ChallengeLogService challengeLogService;

    @Auth
    @GetMapping("/{userChallengeId}/all")
    public ResponseEntity<List<ChallengeLogVO>> getAllLogsByUserChallenge(@PathVariable Long userChallengeId,
                                                                          HttpServletRequest request) {
        List<ChallengeLogVO> logs = challengeLogService.getAllLogsByUserChallenge(userChallengeId);
        return ResponseEntity.ok(logs);
    }

}
