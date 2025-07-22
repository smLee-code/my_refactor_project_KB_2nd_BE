package org.funding.fund.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.dto.FundListResponseDTO;
import org.funding.fund.dto.FundDetailResponseDTO;

import java.util.List;

@Mapper
public interface FundDAO {

    // 펀딩 생성
    void insert(FundVO fundVO);

    // 펀딩 조회 by ID
    FundVO selectById(Long fundId);

    // 펀딩 업데이트
    void update(FundVO fundVO);

    // 펀딩 삭제
    boolean delete(Long fundId);

    // 모든 펀딩 조회
    List<FundVO> selectAll();
    
    // 진행상태 + 펀드타입별 펀딩 목록 조회 (펀드타입은 선택사항)
    List<FundListResponseDTO> selectByProgressAndFundType(@Param("progress") ProgressType progress, @Param("fundType") FundType fundType);
    
    // 펀딩 상세 조회 by ID
    FundDetailResponseDTO selectDetailById(Long fundId);
  
}