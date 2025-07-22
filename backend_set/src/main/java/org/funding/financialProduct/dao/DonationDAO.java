package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.DonationVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DonationDAO {
    void insertDonation(DonationVO vo);
    DonationVO selectByProductId(Long productId);
    void update(DonationVO vo);
    void deleteByProductId(Long productId);
}
