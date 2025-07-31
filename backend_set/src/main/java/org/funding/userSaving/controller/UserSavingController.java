package org.funding.userSaving.controller;

import lombok.RequiredArgsConstructor;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.service.UserSavingService;
import org.funding.userSaving.vo.UserSavingVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userSaving")
@RequiredArgsConstructor
public class UserSavingController {

    private final UserSavingService userSavingService;

    // 저축 상품 가입
    @PostMapping("/apply")
    public ResponseEntity<String> applySaving(@RequestBody UserSavingRequestDTO userSavingRequestDTO) {
        return ResponseEntity.ok(userSavingService.applySaving(userSavingRequestDTO));
    }

    // 저축 상품 해지
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelSaving(@PathVariable Long id) {
        return ResponseEntity.ok(userSavingService.cancelSaving(id));
    }

    // 저축 상품 단건 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserSavingVO> getUserSaving(@PathVariable Long id) {
        return ResponseEntity.ok(userSavingService.findById(id));
    }

    // 유저가 가입한 저축 상품 전체 조회
    @GetMapping("/user/{id}")
    public ResponseEntity<List<UserSavingVO>> getAllUserSaving(@PathVariable Long id) {
        return ResponseEntity.ok(userSavingService.getAllUserSaving(id));
    }
}
