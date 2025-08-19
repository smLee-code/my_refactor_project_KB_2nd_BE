package org.funding.userLoan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(tags = "대출 신청/관리 API")
@RestController
@RequestMapping("/api/user-loan")
@RequiredArgsConstructor
public class UserLoanController {

    private final UserLoanService userLoanService;
    private final MemberDAO memberDAO;
    private final MyPageService myPageService;

    @ApiOperation(value = "대출 신청 (사용자용)", notes = "특정 대출 상품(fundId)에 참여를 신청합니다.")
    @Auth
    @PostMapping("/{id}")
    public ResponseEntity<UserLoanResponseDTO> applyUserLoan(
            @ApiParam(value = "참여할 펀딩(대출) ID", required = true, example = "1") @PathVariable("id") Long id,
            @RequestBody UserLoanRequestDTO userLoanRequestDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.applyLoan(id, userLoanRequestDTO, userId).getBody());
    }

    @ApiOperation(value = "대출 신청 취소 (사용자용)", notes = "신청한 대출을 취소합니다.")
    @Auth
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelUserLoan(
            @ApiParam(value = "취소할 펀딩(대출) ID", required = true, example = "1") @PathVariable("id") Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userLoanService.cancelLoan(id, userId);
        return ResponseEntity.ok("정상적으로 취소가 완료되었습니다.");
    }

    @ApiOperation(value = "대출 신청 승인 (관리자용)", notes = "사용자의 대출 신청을 승인 처리합니다.")
    @Auth
    @PatchMapping("/approve")
    public ResponseEntity<String> approveUserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO,
                                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.approveLoan(approveUserLoanRequestDTO, userId));
    }

    @ApiOperation(value = "대출 신청 반려 (관리자용)", notes = "사용자의 대출 신청을 반려 처리합니다.")
    @Auth
    @PatchMapping("/reject")
    public ResponseEntity<String> rejectUserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO,
                                                 HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.rejectLoan(approveUserLoanRequestDTO, userId));
    }

    @ApiOperation(value = "대출금 지급 (관리자용)", notes = "승인된 대출 신청 건에 대해 대출금을 지급 처리합니다.")
    @Auth
    @PatchMapping("/payment")
    public ResponseEntity<String> UserLoan(@RequestBody ApproveUserLoanRequestDTO approveUserLoanRequestDTO,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userLoanService.processLoanPayment(approveUserLoanRequestDTO, userId));
    }

    @ApiOperation(value = "내가 신청한 대출 목록 조회 (사용자용)", notes = "현재 로그인한 사용자가 신청한 모든 대출 목록을 조회합니다.")
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

    @ApiOperation(value = "특정 대출의 신청자 목록 조회 (관리자용)", notes = "특정 대출 상품(fundId)에 신청한 모든 사용자 목록을 조회합니다. 상태별 필터링이 가능합니다.")
    @Auth
    @GetMapping("/loan/{fundId}/applications")
    public ResponseEntity<List<UserLoanApplicationDTO>> getLoanApplications(
            @ApiParam(value = "조회할 펀딩(대출) ID", required = true, example = "1") @PathVariable("fundId") Long fundId,
            @ApiParam(value = "조회할 상태 (선택)", example = "PENDING") @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {
        Long adminUserId = (Long) request.getAttribute("userId");
        List<UserLoanApplicationDTO> applications = userLoanService.getApplicationsForLoan(fundId, status, adminUserId);
        return ResponseEntity.ok(applications);
    }

    @ApiOperation(value = "대출 신청자 상세 정보 조회 (관리자용)", notes = "대출을 신청한 특정 사용자의 상세 정보를 조회합니다.")
    @Auth
    @GetMapping("/users/{userId}")
    public ResponseEntity<MyPageResponseDTO> getUserDetails(
            @ApiParam(value = "조회할 사용자 ID", required = true, example = "1") @PathVariable("userId") Long targetUserId,
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
