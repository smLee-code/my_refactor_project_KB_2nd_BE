package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.DonationVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DonationDAO {
    // 기부 삽입
    void insertDonation(DonationVO vo);

    // 상품 id로 기부 조회
    DonationVO selectByProductId(Long productId);

    // 업데이트
    void update(DonationVO vo);

    // 삭제
    void deleteByProductId(Long productId);
}
