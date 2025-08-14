package org.funding.userSaving.controller;

import lombok.RequiredArgsConstructor;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserSavingException;
import org.funding.security.util.Auth;
import org.funding.userSaving.dto.UserSavingDetailDTO;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.service.UserSavingService;
import org.funding.userSaving.vo.UserSavingVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user-saving")
@RequiredArgsConstructor
public class UserSavingController {

    private final UserSavingService userSavingService;

    // 저축 상품 가입
    @Auth
    @PostMapping("/{id}")
    public ResponseEntity<String> applySaving(@PathVariable("id") Long id,
                                              @RequestBody UserSavingRequestDTO userSavingRequestDTO,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.applySaving(id, userSavingRequestDTO, userId));
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

    // 유저가 가입한 저축 상품 전체 조회 (디테일적이지않음 사용 x)
    @Auth
    @GetMapping("/user")
    public ResponseEntity<List<UserSavingVO>> getAllUserSaving(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.getAllUserSaving(userId));
    }

    // 유저가 가입한 저축 상품 모아보기
    @Auth
    @GetMapping("/user/all/v2")
    public ResponseEntity<?> getMyAllSavings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new UserSavingException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<UserSavingDetailDTO> mySavings = userSavingService.findMySavings(userId);
        return ResponseEntity.ok(mySavings);
    }
}
