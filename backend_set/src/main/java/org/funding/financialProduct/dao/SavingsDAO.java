package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.SavingsVO;
import org.mapstruct.Mapper;

@Mapper
public interface SavingsDAO {
    void insertSavings(SavingsVO vo);
}
