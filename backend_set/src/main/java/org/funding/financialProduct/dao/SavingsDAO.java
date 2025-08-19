package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.SavingsVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SavingsDAO {
    // 저축 삽입
    void insertSavings(SavingsVO vo);

    // 조회
    SavingsVO selectByProductId(Long productId);

    // 업데이트
    void update(SavingsVO vo);

    // 삭제
    void deleteByProductId(Long productId);
}
