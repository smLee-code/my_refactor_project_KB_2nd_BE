package org.funding.userLoan.controller;

import lombok.RequiredArgsConstructor;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserLoanException;
import org.funding.security.util.Auth;
import org.funding.userLoan.dto.*;
import org.funding.userLoan.service.UserLoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user-loan")
@RequiredArgsConstructor
public class UserLoanController {

    private final UserLoanService userLoanService;

    // 대출 가입 (사용자용)
    @Auth
    @PostMapping("/{id}")
    public ResponseEntity<UserLoanResponseDTO> applyUserLoan(@PathVariable("id") Long id,
                                                             @RequestBody UserLoanRequestDTO userLoanRequestDTO,
                                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.applyLoan(id, userLoanRequestDTO, userId).getBody());
    }

    // 대출 취소 (사용자용)
    @Auth
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelUserLoan(@PathVariable("id") Long id,
                                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userLoanService.cancelLoan(id, userId);
        return ResponseEntity.ok("정상적으로 취소가 완료되었습니다.");
    }

    // 대출 승인 (관리자용)
    @Auth
    @PatchMapping("/approve")
    public ResponseEntity<String> approveUserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO,
                                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.approveLoan(approveUserLoanRequestDTO, userId));
    }

    // 대출 반려 (관리자용)
    @Auth
    @PatchMapping("/reject")
    public ResponseEntity<String> rejectUserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO,
                                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.rejectLoan(approveUserLoanRequestDTO, userId));
    }

    // 대출 지급 (관리자용)
    @Auth
    @PatchMapping("/payment")
    public ResponseEntity<String> UserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.processLoanPayment(approveUserLoanRequestDTO, userId));
    }

    // 유저가 가입한 모든 대출 조회
    @Auth
    @GetMapping("/user/all/v2")
    public ResponseEntity<?> getMyAllLoans(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<UserLoanDetailDTO> myLoans = userLoanService.getAllUserLoans(userId);
        return ResponseEntity.ok(myLoans);
    }



}
