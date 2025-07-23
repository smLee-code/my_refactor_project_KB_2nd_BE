package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.SavingsVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SavingsDAO {
    void insertSavings(SavingsVO vo);
    SavingsVO selectByProductId(Long productId);
    void update(SavingsVO vo);
    void deleteByProductId(Long productId);
}
