package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.DonationVO;
import org.mapstruct.Mapper;

@Mapper
public interface DonationDAO {
    void insertDonation(DonationVO vo);
}
