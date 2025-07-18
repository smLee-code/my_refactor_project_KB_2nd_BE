package org.funding.badge.controller;

import lombok.RequiredArgsConstructor;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.funding.badge.service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    // 뱃지 생성 (관리자용)
    @PostMapping("/create")
    public ResponseEntity<String> createBadge(@RequestBody CreateBadgeDTO createBadgeDTO) {
        badgeService.createBadge(createBadgeDTO);
        return ResponseEntity.ok("뱃지가 정상적으로 등록되었습니다.");
    }

    // 뱃지 수정 (관리자용)
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBadge(@PathVariable Long id, @RequestBody UpdateBadgeDTO updateBadgeDTO) {
        badgeService.updateBadge(updateBadgeDTO, id);
        return ResponseEntity.ok("뱃지가 정상적으로 업데이트 되었습니다");
    }

    // 뱃지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.ok("뱃지가 정상적으로 삭제되었습니다.");
    }

    // 뱃지 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<BadgeResponseDTO> readBadge(@PathVariable Long id) {
        return ResponseEntity.ok(badgeService.getBadge(id));
    }

    // 뱃지 전체 조회
    @GetMapping("/all/badge")
    ResponseEntity<List<BadgeResponseDTO>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

}
