package org.funding.userLoan.controller;

import lombok.RequiredArgsConstructor;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserLoanException;
import org.funding.security.util.Auth;
import org.funding.user.dao.MemberDAO;
import org.funding.user.dto.MyPageResponseDTO;
import org.funding.user.service.MyPageService;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
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
    private final MemberDAO memberDAO;
    private final MyPageService myPageService;

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

    // 특정 대출에 대한 신청 내역 조회
    @Auth
    @GetMapping("/loan/{fundId}/applications")
    public ResponseEntity<List<UserLoanApplicationDTO>> getLoanApplications(
            @PathVariable("fundId") Long fundId,
            @RequestParam(value = "status", required = false) String status, // PENDING, APPROVED, REJECTED, DONE, ALL
            HttpServletRequest request) {

        Long adminUserId = (Long) request.getAttribute("userId");
        List<UserLoanApplicationDTO> applications = userLoanService.getApplicationsForLoan(fundId, status, adminUserId);
        return ResponseEntity.ok(applications);
    }


    // 대출 승인용 특정 사용자 정보 조회
    @Auth
    @GetMapping("/users/{userId}")
    public ResponseEntity<MyPageResponseDTO> getUserDetails(
            @PathVariable("userId") Long targetUserId,
            HttpServletRequest request) {

        Long adminUserId = (Long) request.getAttribute("userId");
        MemberVO admin = memberDAO.findById(adminUserId);
        if (admin == null || admin.getRole() != Role.ROLE_ADMIN) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_ADMIN);
        }

        MyPageResponseDTO userInfo = myPageService.getMyPageInfo(targetUserId);
        return ResponseEntity.ok(userInfo);
    }



}
