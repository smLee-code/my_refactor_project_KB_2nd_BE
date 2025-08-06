package org.funding.userSaving.controller;

import lombok.RequiredArgsConstructor;
import org.funding.security.util.Auth;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.service.UserSavingService;
import org.funding.userSaving.vo.UserSavingVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/userSaving")
@RequiredArgsConstructor
public class UserSavingController {

    private final UserSavingService userSavingService;

    // 저축 상품 가입
    @Auth
    @PostMapping("/apply")
    public ResponseEntity<String> applySaving(@RequestBody UserSavingRequestDTO userSavingRequestDTO,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.applySaving(userSavingRequestDTO, userId));
    }

    // 저축 상품 해지
    @Auth
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelSaving(@PathVariable Long id,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.cancelSaving(id, userId));
    }

    // 저축 상품 단건 상세 조회
    @Auth
    @GetMapping("/{id}")
    public ResponseEntity<UserSavingVO> getUserSaving(@PathVariable Long id,
                                                      HttpServletRequest request) {
        return ResponseEntity.ok(userSavingService.findById(id));
    }

    // 유저가 가입한 저축 상품 전체 조회
    @Auth
    @GetMapping("/user")
    public ResponseEntity<List<UserSavingVO>> getAllUserSaving(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.getAllUserSaving(userId));
    }
}
