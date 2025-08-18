package org.funding.userLoan.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.userLoan.dto.UserLoanApplicationDTO;
import org.funding.userLoan.dto.UserLoanDetailDTO;
import org.funding.userLoan.dto.UserLoanRequestDTO;
import org.funding.userLoan.vo.UserLoanVO;
import org.funding.userLoan.vo.enumType.SuccessType;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserLoanDAO {
    // 대출 가입 신청
    void insertUserLoan(UserLoanVO userLoanVO);

    // id로 대출 내역 조회
    UserLoanVO findById(Long userLoanId);

    // 전체 조회
    List<UserLoanVO> findAll();

    // 대출 내역 수정 (사용 x)
    void updateUserLoan(UserLoanVO userLoanVO);

    // 대출 내역 삭제
    void deleteUserLoan(Long userLoanId);

    // 타입 기준 대출 조회(관리자용)
    List<UserLoanVO> findByLoanAccess(SuccessType loanAccess);

    // 유저 참여했는지 판별
    boolean existsByUserIdAndFundId(@Param("userId") Long userId, @Param("fundId") Long fundId);

    List<UserLoanDetailDTO> findAllLoanDetailsByUserId(@Param("userId") Long userId);

    List<UserLoanApplicationDTO> findApplicationsByFundId(Map<String, Object> params);

    // 펀딩별 가입자 수 조회
    int countByFundId(@Param("fundId") Long fundId);
}
