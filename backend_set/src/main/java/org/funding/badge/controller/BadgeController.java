package org.funding.badge.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.funding.badge.service.BadgeService;
import org.funding.security.util.Auth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "뱃지 API")
@RestController
@RequestMapping("/api/badge")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    // 뱃지 생성 (관리자용)
    @ApiOperation(value = "뱃지 생성 (관리자용)", notes = "새로운 뱃지를 시스템에 등록합니다.")
    @Auth
    @PostMapping("/create")
    public ResponseEntity<String> createBadge(@RequestBody CreateBadgeDTO createBadgeDTO,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        badgeService.createBadge(createBadgeDTO, userId);
        return ResponseEntity.ok("뱃지가 정상적으로 등록되었습니다.");
    }

    // 뱃지 수정 (관리자용)

    @ApiOperation(value = "뱃지 수정 (관리자용)", notes = "기존 뱃지의 정보를 수정합니다.")
    @Auth
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBadge(@PathVariable Long id, @RequestBody UpdateBadgeDTO updateBadgeDTO,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        badgeService.updateBadge(updateBadgeDTO, id, userId);
        return ResponseEntity.ok("뱃지가 정상적으로 업데이트 되었습니다");
    }

    // 뱃지 삭제
    @ApiOperation(value = "뱃지 삭제 (관리자용)", notes = "특정 뱃지를 시스템에서 삭제합니다.")
    @Auth
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBadge(@PathVariable Long id,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        badgeService.deleteBadge(id, userId);
        return ResponseEntity.ok("뱃지가 정상적으로 삭제되었습니다.");
    }

    // 뱃지 단건 조회
    @ApiOperation(value = "뱃지 단건 조회", notes = "특정 뱃지 하나의 상세 정보를 조회합니다.")
    @Auth
    @GetMapping("/{id}")
    public ResponseEntity<BadgeResponseDTO> readBadge(@PathVariable Long id,
                                                      HttpServletRequest request) {
        return ResponseEntity.ok(badgeService.getBadge(id));
    }

    // 뱃지 전체 조회
    @ApiOperation(value = "뱃지 전체 조회", notes = "시스템에 등록된 모든 뱃지 목록을 조회합니다.")
    @Auth
    @GetMapping("/all/badge")
    ResponseEntity<List<BadgeResponseDTO>> getAllBadges(HttpServletRequest request) {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    // 유저가 가진 뱃지 가져오기
    @ApiOperation(value = "사용자가 보유한 뱃지 조회", notes = "현재 로그인한 사용자가 획득한 모든 뱃지 목록을 조회합니다.")
    @Auth
    @GetMapping("/user")
    public ResponseEntity<List<BadgeResponseDTO>> getUserBadges(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<BadgeResponseDTO> userBadges = badgeService.getUserBadges(userId);

        return ResponseEntity.ok(userBadges);
    }
}
