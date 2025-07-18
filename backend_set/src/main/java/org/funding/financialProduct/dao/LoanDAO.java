package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.LoanVO;
import org.mapstruct.Mapper;

@Mapper
public interface LoanDAO {
    void insertLoan(LoanVO vo);
}
