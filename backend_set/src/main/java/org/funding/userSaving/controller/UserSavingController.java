package org.funding.userSaving.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(tags = "저축 참여 API (사용자용)")
@RestController
@RequestMapping("/api/user-saving")
@RequiredArgsConstructor
public class UserSavingController {

    private final UserSavingService userSavingService;

    @ApiOperation(value = "저축 상품 가입", notes = "특정 저축 상품(fundId)에 가입합니다.")
    @Auth
    @PostMapping("/{id}")
    public ResponseEntity<String> applySaving(
            @ApiParam(value = "가입할 펀딩(저축) ID", required = true, example = "1") @PathVariable("id") Long id,
            @RequestBody UserSavingRequestDTO userSavingRequestDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.applySaving(id, userSavingRequestDTO, userId));
    }

    @ApiOperation(value = "저축 상품 해지", notes = "가입한 저축 상품을 해지합니다.")
    @Auth
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelSaving(
            @ApiParam(value = "해지할 펀딩(저축) ID", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.cancelSaving(id, userId));
    }

    @ApiOperation(value = "단일 저축 가입 내역 조회", notes = "특정 저축 가입 내역(userSavingId)을 상세 조회합니다.")
    @Auth
    @GetMapping("/{id}")
    public ResponseEntity<UserSavingVO> getUserSaving(
            @ApiParam(value = "조회할 사용자 저축 ID", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        return ResponseEntity.ok(userSavingService.findById(id));
    }

    @Deprecated // 이 어노테이션은 해당 API가 더 이상 사용되지 않음을 나타냅니다.
    @ApiOperation(value = "나의 전체 저축 내역 조회 (구버전, 사용 X)", notes = "사용자 ID로 모든 저축 가입 내역을 간단하게 조회합니다. (v2 API 사용 권장)")
    @Auth
    @GetMapping("/user")
    public ResponseEntity<List<UserSavingVO>> getAllUserSaving(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userSavingService.getAllUserSaving(userId));
    }

    @ApiOperation(value = "내가 가입한 모든 저축 상품 조회 (신버전)", notes = "현재 로그인한 사용자가 가입한 모든 저축 상품의 상세 목록을 조회합니다.")
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