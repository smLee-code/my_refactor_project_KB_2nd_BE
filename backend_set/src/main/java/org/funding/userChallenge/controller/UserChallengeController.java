package org.funding.userChallenge.controller;

import lombok.RequiredArgsConstructor;
import org.funding.security.util.Auth;
import org.funding.userChallenge.dto.ApplyChallengeRequestDTO;
import org.funding.userChallenge.dto.ChallengeRequestDTO;
import org.funding.userChallenge.dto.DeleteChallengeRequestDTO;
import org.funding.userChallenge.service.UserChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/userChallenge")
@RequiredArgsConstructor
public class UserChallengeController {

    private final UserChallengeService userChallengeService;

    // 첼린지 가입 신청 로직
    @Auth
    @PostMapping("/{id}")
    public ResponseEntity<?> applyChallenge(@PathVariable Long id,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userChallengeService.applyChallenge(id, userId);
        return ResponseEntity.ok("가입이 완료되었습니다");
    }

    // 첼린지 가입 취소 로직
    @Auth
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long id,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userChallengeService.deleteChallenge(id, userId);
        return ResponseEntity.ok("정상적으로 챌린지에 취소되었습니다.");
    }

    // 첼린지 인증 로직
    @Auth
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyChallenge(@PathVariable("id") Long id,
                                             @RequestBody ChallengeRequestDTO challengeRequestDTO,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userChallengeService.verifyDailyChallenge(id, userId, challengeRequestDTO.getImageUrl(), challengeRequestDTO.getDate());
        return ResponseEntity.ok("인증 완료");
    }
}
