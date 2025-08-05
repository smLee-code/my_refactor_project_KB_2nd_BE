package org.funding.payment.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.payment.dto.FundingInfoDTO;

@Mapper
public interface FundingInfoDAO {
    // fund_id로 펀딩 정보 조회 (fund_type 포함)
    FundingInfoDTO selectFundingInfoById(Long fundId);
}