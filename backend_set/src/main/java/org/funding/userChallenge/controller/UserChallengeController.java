package org.funding.userChallenge.controller;

import lombok.RequiredArgsConstructor;
import org.funding.S3.service.S3ImageService;
import org.funding.security.util.Auth;
import org.funding.userChallenge.dto.ApplyChallengeRequestDTO;
import org.funding.userChallenge.dto.ChallengeRequestDTO;
import org.funding.userChallenge.dto.DeleteChallengeRequestDTO;
import org.funding.userChallenge.service.UserChallengeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/userChallenge")
@RequiredArgsConstructor
public class UserChallengeController {

    private final UserChallengeService userChallengeService;
    private final S3ImageService s3ImageService;

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
    @PostMapping(value = "/{id}/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> verifyChallenge(@PathVariable("id") Long id,
                                             @RequestPart("file") MultipartFile file,
                                             @RequestPart("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                             HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        String imageUrl = s3ImageService.uploadSingleImageAndGetUrl(file);
        userChallengeService.verifyDailyChallenge(id, userId, imageUrl, date);
        return ResponseEntity.ok("인증 완료");
    }
}
