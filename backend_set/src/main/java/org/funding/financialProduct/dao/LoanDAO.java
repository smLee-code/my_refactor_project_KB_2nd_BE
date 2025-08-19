package org.funding.financialProduct.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.financialProduct.vo.LoanVO;

import java.util.List;

@Mapper
public interface LoanDAO {
    // 대출 삽입
    void insertLoan(LoanVO vo);

    // 조회
    LoanVO selectByProductId(Long productId);

    // 업데이트
    void update(LoanVO vo);

    // 삭제
    void deleteByProductId(Long productId);

}
