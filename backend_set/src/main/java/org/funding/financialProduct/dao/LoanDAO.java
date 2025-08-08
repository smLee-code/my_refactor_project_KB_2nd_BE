package org.funding.financialProduct.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.financialProduct.vo.LoanVO;

import java.util.List;

@Mapper
public interface LoanDAO {
    void insertLoan(LoanVO vo);
    LoanVO selectByProductId(Long productId);
    void update(LoanVO vo);
    void deleteByProductId(Long productId);

}
