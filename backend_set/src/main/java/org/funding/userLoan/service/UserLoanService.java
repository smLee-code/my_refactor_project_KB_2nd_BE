package org.funding.userLoan.service;

import lombok.RequiredArgsConstructor;
import org.funding.financialProduct.dao.FinancialProductDAO;
import org.funding.financialProduct.dao.LoanDAO;
import org.funding.financialProduct.vo.LoanVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.funding.userLoan.dao.UserLoanDAO;
import org.funding.userLoan.dto.ApproveUserLoanRequestDTO;
import org.funding.userLoan.dto.CancelLoanRequestDTO;
import org.funding.userLoan.dto.UserLoanRequestDTO;
import org.funding.userLoan.dto.UserLoanResponseDTO;
import org.funding.userLoan.vo.UserLoanVO;
import org.funding.userLoan.vo.enumType.SuccessType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoanService {
    private final MemberDAO memberDAO;
    private final FundDAO fundDAO;
    private final UserLoanDAO userLoanDAO;
    private final LoanDAO loanDAO;

    // 대출 승인 신청
    public ResponseEntity<UserLoanResponseDTO> applyLoan(Long fundId, UserLoanRequestDTO loanRequestDTO, Long userId) {
        // 멤버 예외처리
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new RuntimeException("해당 멤버는 존재하지 않습니다.");
        }

        FundVO fund = fundDAO.selectById(fundId);
        if (fund == null) {
            throw new RuntimeException("해당 펀딩은 존재하지 않습니다.");
        }

        if (fund.getProgress() == ProgressType.End) {
            throw new RuntimeException("해당 펀딩은 종료되었습니다.");
        }

        Long productId = fund.getProductId();
        LoanVO loan = loanDAO.selectByProductId(productId);

        // 대출 한도 예외처리
        Integer amount = loanRequestDTO.getLoanAmount();
        if (amount > loan.getLoanLimit()) {
            throw new RuntimeException("대출 한도는 넘었습니다.");
        }

        UserLoanVO userLoan = new UserLoanVO();
        userLoan.setUserId(userId);
        userLoan.setFundId(fundId);
        userLoan.setLoanAmount(loanRequestDTO.getLoanAmount());
        userLoan.setLoanAccess(SuccessType.PENDING);
        userLoan.setFullPayment(false);
        userLoanDAO.insertUserLoan(userLoan);

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
            throw new RuntimeException("해당 멤버는 존재하지 않습니다.");
        }

        // 신청 여부 예외처리
        UserLoanVO userLoan = userLoanDAO.findById(userLoanId);
        if (userLoan == null) {
            throw new RuntimeException("취소하실 신청내역이 존재하지 않습니다.");
        }

        // 지급 완료 여부 예외처리
        if (userLoan.getLoanAccess() == SuccessType.DONE) {
            throw new RuntimeException("이미 지급완료된 대출이라 취소하실 수 없습니다");
        }

        userLoanDAO.deleteUserLoan(userLoanId);
    }

    // 대출 승인 (관리자용)
    public String approveLoan(ApproveUserLoanRequestDTO approveUserLoanRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("관리자만 접근 가능합니다.");
        }

        Long userLoanId = approveUserLoanRequestDTO.getUserLoanId();

        validateMember(userId);
        UserLoanVO userLoan = validateLoan(userLoanId);

        if (userLoan.getLoanAccess() != SuccessType.PENDING) {
            throw new RuntimeException("이미 처리된 신청입니다.");
        }

        userLoan.setLoanAccess(SuccessType.APPROVED);
        userLoanDAO.updateUserLoan(userLoan);

        return "허가 완료";
    }

    // 대출 반려
    public String rejectLoan(ApproveUserLoanRequestDTO approveUserLoanRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("관리자만 접근 가능합니다.");
        }
        Long userLoanId = approveUserLoanRequestDTO.getUserLoanId();

        validateMember(userId);
        UserLoanVO userLoan = validateLoan(userLoanId);

        if (userLoan.getLoanAccess() != SuccessType.PENDING) {
            throw new RuntimeException("이미 처리된 신청입니다.");
        }

        userLoan.setLoanAccess(SuccessType.REJECTED);
        userLoanDAO.updateUserLoan(userLoan);
        return "반려 완료";
    }

    // 대출 지급
    public String processLoanPayment(ApproveUserLoanRequestDTO approveUserLoanRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("관리자만 접근 가능합니다.");
        }
        Long userLoanId = approveUserLoanRequestDTO.getUserLoanId();

        validateMember(userId);
        UserLoanVO userLoan = validateLoan(userLoanId);

        if (userLoan.getLoanAccess() != SuccessType.APPROVED) {
            throw new RuntimeException("지급은 승인된 후에만 가능합니다.");
        }

        userLoan.setLoanAccess(SuccessType.DONE);
        userLoanDAO.updateUserLoan(userLoan);
        return "지급 완료";
    }

    // 공통 검증 메서드
    private void validateMember(Long userId) {
        if (memberDAO.findById(userId) == null) {
            throw new RuntimeException("유효하지 않은 유저입니다.");
        }
    }

    private UserLoanVO validateLoan(Long userLoanId) {
        UserLoanVO loan = userLoanDAO.findById(userLoanId);
        if (loan == null) {
            throw new RuntimeException("신청 내역이 존재하지 않습니다.");
        }
        return loan;
    }

}
