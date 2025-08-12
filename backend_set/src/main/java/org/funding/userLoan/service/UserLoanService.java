package org.funding.userLoan.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.badge.service.BadgeService;
import org.funding.financialProduct.dao.FinancialProductDAO;
import org.funding.financialProduct.dao.LoanDAO;
import org.funding.financialProduct.vo.LoanVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserLoanException;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.funding.userLoan.dao.UserLoanDAO;
import org.funding.userLoan.dto.*;
import org.funding.userLoan.vo.UserLoanVO;
import org.funding.userLoan.vo.enumType.SuccessType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLoanService {
    private final MemberDAO memberDAO;
    private final FundDAO fundDAO;
    private final UserLoanDAO userLoanDAO;
    private final LoanDAO loanDAO;
    private final BadgeService badgeService;
    private final S3ImageDAO s3ImageDAO;

    // 대출 승인 신청
    public ResponseEntity<UserLoanResponseDTO> applyLoan(Long fundId, UserLoanRequestDTO loanRequestDTO, Long userId) {
        // 멤버 예외처리
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_FOUND);
        }

        FundVO fund = fundDAO.selectById(fundId);
        if (fund == null) {
            throw new UserLoanException(ErrorCode.FUNDING_NOT_FOUND);
        }

        if (fund.getProgress() == ProgressType.End) {
            throw new UserLoanException(ErrorCode.END_FUND);
        }

        Long productId = fund.getProductId();
        LoanVO loan = loanDAO.selectByProductId(productId);

        // 대출 한도 예외처리
        Integer amount = loanRequestDTO.getLoanAmount();
        if (amount > loan.getLoanLimit()) {
            throw new UserLoanException(ErrorCode.AMOUNT_OVER);
        }

        UserLoanVO userLoan = new UserLoanVO();
        userLoan.setUserId(userId);
        userLoan.setFundId(fundId);
        userLoan.setLoanAmount(loanRequestDTO.getLoanAmount());
        userLoan.setLoanAccess(SuccessType.PENDING);
        userLoan.setFullPayment(false);
        userLoanDAO.insertUserLoan(userLoan);

        // 뱃지 권한 부여
        badgeService.checkAndGrantBadges(userId);

        UserLoanResponseDTO userLoanResponseDTO = new UserLoanResponseDTO();
        userLoanResponseDTO.setUserName(member.getUsername());
        userLoanResponseDTO.setLoanAmount(loanRequestDTO.getLoanAmount());

        return ResponseEntity.ok(userLoanResponseDTO);
    }

    // 대출 승인 취소
    public void cancelLoan(Long userLoanId, Long userId) {
        // 회원 예외처리
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 신청 여부 예외처리
        UserLoanVO userLoan = userLoanDAO.findById(userLoanId);
        if (userLoan == null) {
            throw new UserLoanException(ErrorCode.NOT_FOUND_LOAN);
        }

        // 지급 완료 여부 예외처리
        if (userLoan.getLoanAccess() == SuccessType.DONE) {
            throw new UserLoanException(ErrorCode.ALREADY_LOAN);
        }

        userLoanDAO.deleteUserLoan(userLoanId);
    }

    // 대출 승인 (관리자용)
    public String approveLoan(ApproveUserLoanRequestDTO approveUserLoanRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_ADMIN);
        }

        Long userLoanId = approveUserLoanRequestDTO.getUserLoanId();

        validateMember(userId);
        UserLoanVO userLoan = validateLoan(userLoanId);

        if (userLoan.getLoanAccess() != SuccessType.PENDING) {
            throw new UserLoanException(ErrorCode.ALREADY_ACCEPT);
        }

        userLoan.setLoanAccess(SuccessType.APPROVED);
        userLoanDAO.updateUserLoan(userLoan);

        return "허가 완료";
    }

    // 대출 반려
    public String rejectLoan(ApproveUserLoanRequestDTO approveUserLoanRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_ADMIN);
        }
        Long userLoanId = approveUserLoanRequestDTO.getUserLoanId();

        validateMember(userId);
        UserLoanVO userLoan = validateLoan(userLoanId);

        if (userLoan.getLoanAccess() != SuccessType.PENDING) {
            throw new UserLoanException(ErrorCode.ALREADY_ACCEPT);
        }

        userLoan.setLoanAccess(SuccessType.REJECTED);
        userLoanDAO.updateUserLoan(userLoan);
        return "반려 완료";
    }

    // 대출 지급
    public String processLoanPayment(ApproveUserLoanRequestDTO approveUserLoanRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_ADMIN);
        }
        Long userLoanId = approveUserLoanRequestDTO.getUserLoanId();

        validateMember(userId);
        UserLoanVO userLoan = validateLoan(userLoanId);

        if (userLoan.getLoanAccess() != SuccessType.APPROVED) {
            throw new UserLoanException(ErrorCode.NOT_PAYMENT);
        }

        userLoan.setLoanAccess(SuccessType.DONE);
        userLoanDAO.updateUserLoan(userLoan);
        return "지급 완료";
    }

    // 유저가 가입한 대출 모아보기
    public List<UserLoanDetailDTO> getAllUserLoans(Long userId) {

        List<UserLoanDetailDTO> myLoans = userLoanDAO.findAllLoanDetailsByUserId(userId);

        if (myLoans == null || myLoans.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = myLoans.stream()
                .map(UserLoanDetailDTO::getProductId)
                .collect(Collectors.toList());

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Funding, productIds);

        Map<Long, List<S3ImageVO>> imagesByProductId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));
        myLoans.forEach(loan ->
                loan.setImages(imagesByProductId.getOrDefault(loan.getProductId(), Collections.emptyList()))
        );

        return myLoans;
    }


    // 관리자용: 특정 대출 상품의 모든 신청 내역 조회
    public List<UserLoanApplicationDTO> getApplicationsForLoan(Long fundId, String status, Long adminUserId) {
        FundVO fund = fundDAO.selectById(fundId);
        if (fund == null) {
            throw new UserLoanException(ErrorCode.FUNDING_NOT_FOUND);
        }
        if (!fund.getUploadUserId().equals(adminUserId)) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_ADMIN); // 또는 더 적절한 에러 코드로 변경
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fundId", fundId);
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            params.put("status", status);
        }

        return userLoanDAO.findApplicationsByFundId(params);
    }

    // 공통 검증 메서드
    private void validateMember(Long userId) {
        if (memberDAO.findById(userId) == null) {
            throw new UserLoanException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    private UserLoanVO validateLoan(Long userLoanId) {
        UserLoanVO loan = userLoanDAO.findById(userLoanId);
        if (loan == null) {
            throw new UserLoanException(ErrorCode.NOT_FOUND_LOAN);
        }
        return loan;
    }

}
