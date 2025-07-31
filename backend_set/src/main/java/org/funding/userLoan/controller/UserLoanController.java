package org.funding.userLoan.controller;

import lombok.RequiredArgsConstructor;
import org.funding.userLoan.dto.ApproveUserLoanRequestDTO;
import org.funding.userLoan.dto.CancelLoanRequestDTO;
import org.funding.userLoan.dto.UserLoanRequestDTO;
import org.funding.userLoan.dto.UserLoanResponseDTO;
import org.funding.userLoan.service.UserLoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-loan")
@RequiredArgsConstructor
public class UserLoanController {

    private final UserLoanService userLoanService;

    // 대출 가입 (사용자용)
    @PostMapping("/{id}")
    public ResponseEntity<UserLoanResponseDTO> applyUserLoan(@PathVariable("id") Long id, @RequestBody UserLoanRequestDTO userLoanRequestDTO) {
        return ResponseEntity.ok(userLoanService.applyLoan(id, userLoanRequestDTO).getBody());
    }

    // 대출 취소 (사용자용)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelUserLoan(@PathVariable("id") Long id, @RequestBody CancelLoanRequestDTO cancelLoanRequestDTO) {
        userLoanService.cancelLoan(id, cancelLoanRequestDTO);
        return ResponseEntity.ok("정상적으로 취소가 완료되었습니다.");
    }

    // 대출 승인 (관리자용)
    @PatchMapping("/approve")
    public ResponseEntity<String> approveUserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO) {
        return ResponseEntity.ok(userLoanService.approveLoan(approveUserLoanRequestDTO));
    }

    // 대출 반려 (관리자용)
    @PatchMapping("/reject")
    public ResponseEntity<String> rejectUserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO) {
        return ResponseEntity.ok(userLoanService.rejectLoan(approveUserLoanRequestDTO));
    }

    // 대출 지급 (관리자용)
    @PatchMapping("/payment")
    public ResponseEntity<String> UserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO) {
        return ResponseEntity.ok(userLoanService.processLoanPayment(approveUserLoanRequestDTO));
    }


}
