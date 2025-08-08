package org.funding.userChallenge.controller;

import lombok.RequiredArgsConstructor;
import org.funding.S3.service.S3ImageService;
import org.funding.security.util.Auth;
import org.funding.userChallenge.dto.*;
import org.funding.userChallenge.service.UserChallengeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user-challenge")
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
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("date") String date,
                                             HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate localDate = LocalDate.parse(date);
        String imageUrl = s3ImageService.uploadSingleImageAndGetUrl(file);
        userChallengeService.verifyDailyChallenge(id, userId, imageUrl, localDate);
        return ResponseEntity.ok("인증 완료");
    }

    // 유저가 참여한 모든 챌린지 조회
    @Auth
    @GetMapping("/user/all/v2")
    public ResponseEntity<?> getAllMyChallenges(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // userId가 없는 경우 예외 처리 (필요 시)
        if (userId == null) {
            return ResponseEntity.status(401).body("인증 정보가 유효하지 않습니다.");
        }
        List<UserChallengeDetailDTO> myChallenges = userChallengeService.findMyChallenges(userId);
        return ResponseEntity.ok(myChallenges);
    }

    // 챌린지 상세보기
    @Auth
    @GetMapping("/{userChallengeId}")
    public ResponseEntity<?> getChallengeDetail(@PathVariable Long userChallengeId, HttpServletRequest request) {
        // 서비스를 통해 챌린지 상세 정보 조회
        ChallengeDetailResponseDTO responseDTO = userChallengeService.getChallengeDetails(userChallengeId);

        if (responseDTO == null) {
            return ResponseEntity.status(404).body("해당 챌린지를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(responseDTO);
    }
}
