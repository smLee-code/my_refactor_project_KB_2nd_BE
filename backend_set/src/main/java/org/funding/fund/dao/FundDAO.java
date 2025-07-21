package org.funding.fund.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.fund.vo.FundVO;

import java.util.List;

@Mapper
public interface FundDAO {

    // 펀딩 생성
    void insert(FundVO fundVO);

    // 펀딩 조회 by ID
    FundVO selectById(Long fundId);

    // 펀딩 업데이트
    FundVO update(FundVO fundVO);

    // 펀딩 삭제
    boolean delete(Long fundId);

    // 모든 펀딩 조회
    List<FundVO> selectAll();
}