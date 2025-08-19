package org.funding.userChallenge.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.S3.service.S3ImageService;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserChallengeException;
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

@Api(tags = "챌린지 참여 API (사용자용)")
@RestController
@RequestMapping("/api/user-challenge")
@RequiredArgsConstructor
public class UserChallengeController {

    private final UserChallengeService userChallengeService;
    private final S3ImageService s3ImageService;

    @ApiOperation(value = "챌린지 참여(가입)", notes = "특정 챌린지(fundId)에 참여합니다.")
    @Auth
    @PostMapping("/{id}")
    public ResponseEntity<?> applyChallenge(
            @ApiParam(value = "참여할 펀딩(챌린지) ID", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userChallengeService.applyChallenge(id, userId);
        return ResponseEntity.ok("가입이 완료되었습니다");
    }

    @ApiOperation(value = "챌린지 참여 취소", notes = "참여중인 챌린지(fundId)를 포기하고 참여를 취소합니다.")
    @Auth
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChallenge(
            @ApiParam(value = "취소할 펀딩(챌린지) ID", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userChallengeService.deleteChallenge(id, userId);
        return ResponseEntity.ok("정상적으로 챌린지에서 취소되었습니다.");
    }

    @ApiOperation(value = "일일 챌린지 인증", notes = "참여중인 챌린지(userChallengeId)에 대해 오늘의 인증을 수행합니다. 이미지 파일과 날짜를 전송해야 합니다.")
    @Auth
    @PostMapping(value = "/{id}/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> verifyChallenge(
            @ApiParam(value = "사용자 챌린지 ID", required = true, example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "인증할 이미지 파일", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "인증 날짜 (YYYY-MM-DD)", required = true, example = "2025-08-19") @RequestParam("date") String date,
            HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate localDate = LocalDate.parse(date);
        String imageUrl = s3ImageService.uploadSingleImageAndGetUrl(file);
        userChallengeService.verifyDailyChallenge(id, userId, imageUrl, localDate);
        return ResponseEntity.ok("인증 완료");
    }

    @ApiOperation(value = "내가 참여한 모든 챌린지 조회", notes = "현재 로그인한 사용자가 참여하고 있는 모든 챌린지 목록을 조회합니다.")
    @Auth
    @GetMapping("/user/all/v2")
    public ResponseEntity<?> getAllMyChallenges(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new UserChallengeException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<UserChallengeDetailDTO> myChallenges = userChallengeService.findMyChallenges(userId);
        return ResponseEntity.ok(myChallenges);
    }

    @ApiOperation(value = "참여중인 챌린지 상세 조회", notes = "사용자가 참여중인 특정 챌린지(userChallengeId)의 상세 정보와 일별 인증 기록을 함께 조회합니다.")
    @Auth
    @GetMapping("/{userChallengeId}")
    public ResponseEntity<?> getChallengeDetail(
            @ApiParam(value = "사용자 챌린지 ID", required = true, example = "1") @PathVariable Long userChallengeId,
            HttpServletRequest request) {
        ChallengeDetailResponseDTO responseDTO = userChallengeService.getChallengeDetails(userChallengeId);
        if (responseDTO == null) {
            throw new UserChallengeException(ErrorCode.NOT_FOUND_CHALLENGE);
        }
        return ResponseEntity.ok(responseDTO);
    }
}