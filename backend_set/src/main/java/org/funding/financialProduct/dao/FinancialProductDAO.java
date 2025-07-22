package org.funding.financialProduct.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.financialProduct.vo.FinancialProductVO;
import org.funding.fund.vo.enumType.FundType;

import java.util.List;

@Mapper
public interface FinancialProductDAO {
    
    // 금융상품 생성
    void insert(FinancialProductVO productVO);
    
    // 아이디로 금융상품 찾기
    FinancialProductVO selectById(Long productId);
    
    // 타입으로 금융상품 찾기
    List<FinancialProductVO> selectByFundType(FundType fundType);
    
    // 금융상품 수정
    void update(FinancialProductVO productVO);
    
    // 금융상품 삭제
    boolean delete(Long productId);
    
    //모든 금융상품 조회
    List<FinancialProductVO> selectAll();
}