package org.funding.userLoan.controller;

import lombok.RequiredArgsConstructor;
import org.funding.security.util.Auth;
import org.funding.userLoan.dto.ApproveUserLoanRequestDTO;
import org.funding.userLoan.dto.CancelLoanRequestDTO;
import org.funding.userLoan.dto.UserLoanRequestDTO;
import org.funding.userLoan.dto.UserLoanResponseDTO;
import org.funding.userLoan.service.UserLoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    // 유저가 신청한 대출 내역
    @Auth
    @GetMapping("/users/loan")
    public ResponseEntity<?> getUserLoan(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

    }


}
