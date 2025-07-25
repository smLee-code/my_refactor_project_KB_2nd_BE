package org.funding.userChallenge.controller;

import lombok.RequiredArgsConstructor;
import org.funding.userChallenge.dto.ApplyChallengeRequestDTO;
import org.funding.userChallenge.dto.ChallengeRequestDTO;
import org.funding.userChallenge.service.UserChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userChallenge")
@RequiredArgsConstructor
public class UserChallengeController {

    private final UserChallengeService userChallengeService;

    // 첼린지 가입 신청 로직
    @PostMapping("/{id}")
    public ResponseEntity<?> applyChallenge(@PathVariable Long id, @RequestBody ApplyChallengeRequestDTO challengeRequestDTO) {
        userChallengeService.applyChallenge(id, challengeRequestDTO);
        return ResponseEntity.ok("가입이 완료되었습니다");
    }


    // 첼린지 인증 로직
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyChallenge(@PathVariable("id") Long id, @RequestBody ChallengeRequestDTO challengeRequestDTO) {
        userChallengeService.verifyDailyChallenge(id, challengeRequestDTO.getUserId(), challengeRequestDTO.getImageUrl(), challengeRequestDTO.getDate());
        return ResponseEntity.ok("인증 완료");
    }
}
